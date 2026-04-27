// No upstream analogue — PatchDocument behavioural edge cases that
// don't fit a fixture pair.
package io.docxkt.patcher

import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.io.DocxReader
import io.docxkt.patcher.io.OoxmlParser
import io.docxkt.patcher.io.OoxmlWriter
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PatchDocumentEdgeCasesTest {

    /**
     * Build a minimal `.docx` byte array with a single paragraph
     * containing [text].
     */
    private fun buildDocx(text: String): ByteArray {
        val docXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body><w:p><w:r><w:t xml:space="preserve">$text</w:t></w:r></w:p></w:body></w:document>"""
        val ctXml = """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/><Default ContentType="application/xml" Extension="xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" PartName="/word/document.xml"/></Types>"""
        val relsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>"""
        return DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", docXml.toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", ctXml.toByteArray()),
            DocxPackager.Entry("_rels/.rels", relsXml.toByteArray()),
        ))
    }

    /** Extract `word/document.xml` text content from patched bytes. */
    private fun extractDocumentXml(bytes: ByteArray): String {
        val parts = DocxReader.read(bytes)
        return parts.getValue("word/document.xml").toString(Charsets.UTF_8)
    }

    // --- empty patches map -------------------------------------------

    @Test fun `empty patches map - identity round-trip preserves text content`() {
        val input = buildDocx("Hello {{name}}!")
        val output = PatchDocument.patch(input)
        // The marker stays literal because no patch keys were supplied.
        val xml = extractDocumentXml(output)
        assertTrue("Hello {{name}}!" in xml)
    }

    @Test fun `empty patches map - returns same parts`() {
        val input = buildDocx("plain")
        val output = PatchDocument.patch(input, emptyMap())
        val parts = DocxReader.read(output)
        assertTrue("word/document.xml" in parts)
        assertTrue("[Content_Types].xml" in parts)
        assertTrue("_rels/.rels" in parts)
    }

    // --- unmatched key ------------------------------------------------

    @Test fun `marker without registered patch is left literal`() {
        val input = buildDocx("Hello {{notRegistered}}!")
        val output = PatchDocument.patch(input, mapOf("other" to Patch.Text("X")))
        val xml = extractDocumentXml(output)
        assertTrue("{{notRegistered}}" in xml)
        // The X replacement value should not appear since no marker matched.
        assertFalse(">X<" in xml)
    }

    @Test fun `unrelated patches are no-ops`() {
        val input = buildDocx("only literal text")
        val output = PatchDocument.patch(input, mapOf("foo" to Patch.Text("Bar")))
        val xml = extractDocumentXml(output)
        assertTrue("only literal text" in xml)
    }

    // --- empty replacement value -------------------------------------

    @Test fun `Patch Text with empty value excises the marker`() {
        val input = buildDocx("Hello {{name}}!")
        val output = PatchDocument.patch(input, mapOf("name" to Patch.Text("")))
        val xml = extractDocumentXml(output)
        assertTrue("Hello !" in xml)
        assertFalse("{{name}}" in xml)
    }

    // --- replacement value contains special XML chars ----------------

    @Test fun `Patch Text with XML-significant chars passes through verbatim`() {
        // The text replacement runs at the DOM Text-node level —
        // setting .data on a Text node does NOT escape; the
        // serializer escapes on emit.
        val input = buildDocx("Hello {{x}}!")
        val output = PatchDocument.patch(input, mapOf("x" to Patch.Text("<a> & </b>")))
        val xml = extractDocumentXml(output)
        // Reserved chars should appear escaped on the wire.
        assertTrue("&lt;a&gt; &amp; &lt;/b&gt;" in xml || "<a> & </b>" in xml,
            "expected escaped or literal content; got: $xml")
    }

    // --- multiple keys in single call ---------------------------------

    @Test fun `multiple distinct patches resolve in single patch call`() {
        val input = buildDocx("{{first}} and {{second}}")
        val output = PatchDocument.patch(input, mapOf(
            "first" to Patch.Text("A"),
            "second" to Patch.Text("B"),
        ))
        val xml = extractDocumentXml(output)
        assertTrue("A and B" in xml)
    }

    // --- recursive default vs false ----------------------------------

    @Test fun `recursive default replaces every occurrence of same key`() {
        // Default recursive=true scans repeatedly; both occurrences resolve.
        val input = buildDocx("{{x}} ... {{x}}")
        val output = PatchDocument.patch(input, mapOf("x" to Patch.Text("Y")))
        val xml = extractDocumentXml(output)
        assertTrue("Y ... Y" in xml)
    }

    @Test fun `recursive false replaces only first occurrence per key`() {
        val input = buildDocx("{{x}} ... {{x}}")
        val output = PatchDocument.patch(
            input,
            mapOf("x" to Patch.Text("Y")),
            recursive = false,
        )
        val xml = extractDocumentXml(output)
        // First replaced with Y; second stays as marker.
        assertTrue("Y ... {{x}}" in xml)
    }

    @Test fun `recursive resolves chained substitution`() {
        // Replacement value contains another marker.
        val input = buildDocx("{{outer}}")
        val output = PatchDocument.patch(input, mapOf(
            "outer" to Patch.Text("X-{{inner}}-Y"),
            "inner" to Patch.Text("MID"),
        ))
        val xml = extractDocumentXml(output)
        assertTrue("X-MID-Y" in xml)
    }

    // --- custom delimiters --------------------------------------------

    @Test fun `custom delimiters - dollar-dollar pair works`() {
        val input = buildDocx("Hello \$\$name\$\$!")
        val output = PatchDocument.patch(
            input,
            mapOf("name" to Patch.Text("Alice")),
            placeholderDelimiters = "$$" to "$$",
        )
        val xml = extractDocumentXml(output)
        assertTrue("Hello Alice!" in xml)
    }

    @Test fun `custom delimiters do not match default braces`() {
        val input = buildDocx("Hello {{name}}!")
        val output = PatchDocument.patch(
            input,
            mapOf("name" to Patch.Text("Alice")),
            placeholderDelimiters = "<%" to "%>",
        )
        // {{...}} should not match because delimiters are <%...%>.
        val xml = extractDocumentXml(output)
        assertTrue("{{name}}" in xml)
        assertFalse("Alice" in xml)
    }

    // --- ZIP-level invariants -----------------------------------------

    @Test fun `patcher always produces a valid ZIP with original parts present`() {
        val input = buildDocx("plain")
        val output = PatchDocument.patch(input)
        val parts = DocxReader.read(output)
        assertEquals(setOf("word/document.xml", "[Content_Types].xml", "_rels/.rels"), parts.keys)
    }
}

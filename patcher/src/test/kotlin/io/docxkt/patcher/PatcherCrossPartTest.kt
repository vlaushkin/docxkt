// Patches apply to header/footer parts in addition to
// word/document.xml. Mirrors upstream `from-docx.ts:245`'s
// per-part loop. Hyperlink/image rIds for header/footer parts
// land on their own `_rels/{part}.xml.rels` file.
package io.docxkt.patcher

import io.docxkt.pack.toByteArray
import io.docxkt.api.runs
import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.io.DocxReader
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class PatcherCrossPartTest {

    @Test fun `text patch in header is replaced and footer is untouched`() {
        val input = buildHeaderFooterDocx(
            bodyText = "body {{name}}",
            headerText = "header {{name}}",
            footerText = "footer literal",
        )
        val output = PatchDocument.patch(
            input,
            mapOf("name" to Patch.Text("Alice")),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        val hdrXml = parts.getValue("word/header1.xml").toString(Charsets.UTF_8)
        val ftrXml = parts.getValue("word/footer1.xml").toString(Charsets.UTF_8)
        assertTrue("body Alice" in docXml, "body should be patched")
        assertTrue("header Alice" in hdrXml, "header should be patched")
        assertTrue("footer literal" in ftrXml, "footer untouched")
    }

    @Test fun `inline hyperlink patch in footer writes rels to footer1 rels`() {
        val input = buildHeaderFooterDocx(
            bodyText = "body literal",
            headerText = "header literal",
            footerText = "footer {{link}}",
        )
        val output = PatchDocument.patch(
            input,
            mapOf(
                "link" to Patch.ParagraphInline(
                    runs { hyperlink("https://example.com/news") { run("BBC") } }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val ftrXml = parts.getValue("word/footer1.xml").toString(Charsets.UTF_8)
        val ftrRelsXml = parts.getValue("word/_rels/footer1.xml.rels").toString(Charsets.UTF_8)
        assertTrue(
            "<w:hyperlink" in ftrXml,
            "expected <w:hyperlink> in patched footer1.xml. Got: $ftrXml",
        )
        assertTrue(
            """Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink"""" in ftrRelsXml,
            "footer1 rels missing hyperlink Type. Got: $ftrRelsXml",
        )
        assertTrue(
            """Target="https://example.com/news"""" in ftrRelsXml,
            "footer1 rels missing target. Got: $ftrRelsXml",
        )
    }

    private fun buildHeaderFooterDocx(
        bodyText: String,
        headerText: String,
        footerText: String,
    ): ByteArray {
        val w = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
        val r = "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
        val docXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="$w" xmlns:r="$r"><w:body><w:p><w:r><w:t xml:space="preserve">$bodyText</w:t></w:r></w:p><w:sectPr><w:headerReference w:type="default" r:id="rId10"/><w:footerReference w:type="default" r:id="rId11"/></w:sectPr></w:body></w:document>"""
        val hdrXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:hdr xmlns:w="$w" xmlns:r="$r"><w:p><w:r><w:t xml:space="preserve">$headerText</w:t></w:r></w:p></w:hdr>"""
        val ftrXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:ftr xmlns:w="$w" xmlns:r="$r"><w:p><w:r><w:t xml:space="preserve">$footerText</w:t></w:r></w:p></w:ftr>"""
        val ctXml = """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/><Default ContentType="application/xml" Extension="xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" PartName="/word/document.xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml" PartName="/word/header1.xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml" PartName="/word/footer1.xml"/></Types>"""
        val pkgRelsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>"""
        val docRelsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId10" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/header" Target="header1.xml"/><Relationship Id="rId11" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/footer" Target="footer1.xml"/></Relationships>"""
        return DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", docXml.toByteArray()),
            DocxPackager.Entry("word/header1.xml", hdrXml.toByteArray()),
            DocxPackager.Entry("word/footer1.xml", ftrXml.toByteArray()),
            DocxPackager.Entry("word/_rels/document.xml.rels", docRelsXml.toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", ctXml.toByteArray()),
            DocxPackager.Entry("_rels/.rels", pkgRelsXml.toByteArray()),
        ))
    }
}

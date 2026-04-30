// Synthetic test: ExternalHyperlink inside a
// Patch.ParagraphInline patch. Verifies the patcher allocates a
// per-part hyperlink rId, registers a `.../hyperlink` rel with
// TargetMode="External" in word/_rels/document.xml.rels, and
// rewrites the snippet's <w:hyperlink r:id="…"> to reference
// that rId.
package io.docxkt.patcher

import io.docxkt.pack.toByteArray
import io.docxkt.api.runs
import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.io.DocxReader
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class PatcherHyperlinkInlineTest {

    @Test fun `hyperlink in inline patch — allocates rId, writes External rel, emits w hyperlink`() {
        val input = buildSimpleDocx("Visit {{link}} today.")
        val output = PatchDocument.patch(
            input,
            mapOf(
                "link" to Patch.ParagraphInline(
                    runs {
                        hyperlink("https://example.com/news") {
                            run("BBC News")
                        }
                    }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        val relsXml = parts.getValue("word/_rels/document.xml.rels").toString(Charsets.UTF_8)

        // The hyperlink in document.xml carries an r:id attribute.
        val hyperlinkRid = Regex("""<w:hyperlink[^>]*r:id="(rId\d+)"""")
            .find(docXml)?.groupValues?.get(1)
            ?: error("expected <w:hyperlink r:id=\"rIdN\"> in patched document.xml. Got: $docXml")

        // The rels file has a Relationship of type .../hyperlink with
        // matching Id, Target, and TargetMode="External".
        assertTrue(
            """Id="$hyperlinkRid"""" in relsXml,
            "rels missing Id=\"$hyperlinkRid\". Got: $relsXml",
        )
        assertTrue(
            """Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink"""" in relsXml,
            "rels missing hyperlink Type. Got: $relsXml",
        )
        assertTrue(
            """Target="https://example.com/news"""" in relsXml,
            "rels missing Target. Got: $relsXml",
        )
        assertTrue(
            """TargetMode="External"""" in relsXml,
            "rels missing TargetMode=External. Got: $relsXml",
        )

        // The hyperlink wraps the patched run text.
        assertTrue(
            "BBC News" in docXml,
            "expected hyperlink content 'BBC News' in patched document.xml.",
        )
    }

    @Test fun `hyperlink rId follows max-existing-plus-one`() {
        // Pre-seed the rels file with rId7 so the new hyperlink
        // gets rId8.
        val docXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body><w:p><w:r><w:t xml:space="preserve">x {{k}}</w:t></w:r></w:p></w:body></w:document>"""
        val ctXml = """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/><Default ContentType="application/xml" Extension="xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" PartName="/word/document.xml"/></Types>"""
        val docRelsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId7" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/></Relationships>"""
        val pkgRelsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>"""
        val input = DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", docXml.toByteArray()),
            DocxPackager.Entry("word/_rels/document.xml.rels", docRelsXml.toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", ctXml.toByteArray()),
            DocxPackager.Entry("_rels/.rels", pkgRelsXml.toByteArray()),
        ))
        val output = PatchDocument.patch(
            input,
            mapOf(
                "k" to Patch.ParagraphInline(
                    runs { hyperlink("https://a.test/") { run("a") } }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val out = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        assertTrue(
            """r:id="rId8"""" in out,
            "expected rId8 (max existing 7 + 1) in patched doc. Got: $out",
        )
    }

    private fun buildSimpleDocx(text: String): ByteArray {
        val docXml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body><w:p><w:r><w:t xml:space="preserve">$text</w:t></w:r></w:p></w:body></w:document>"""
        val ctXml = """<?xml version="1.0" encoding="UTF-8"?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/><Default ContentType="application/xml" Extension="xml"/><Override ContentType="application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml" PartName="/word/document.xml"/></Types>"""
        val pkgRelsXml = """<?xml version="1.0" encoding="UTF-8"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships"><Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="word/document.xml"/></Relationships>"""
        return DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", docXml.toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", ctXml.toByteArray()),
            DocxPackager.Entry("_rels/.rels", pkgRelsXml.toByteArray()),
        ))
    }
}

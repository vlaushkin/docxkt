// DOCUMENT-type patches admit mixed paragraph children (hyperlinks
// + images) and Table-at-top-level snippets.
package io.docxkt.patcher

import io.docxkt.api.paragraphs
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.io.DocxReader
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

internal class PatcherDocumentMixedTest {

    @Test fun `paragraph snippet with hyperlink resolves rId and writes External rel`() {
        val input = buildSimpleDocx("Header\n\n{{block}}\n\nFooter")
        val output = PatchDocument.patch(
            input,
            mapOf(
                "block" to Patch.Paragraphs(
                    paragraphs {
                        paragraph {
                            text("Visit ")
                            hyperlink("https://example.com") { text("link") }
                        }
                    }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        val relsXml = parts.getValue("word/_rels/document.xml.rels").toString(Charsets.UTF_8)

        val rid = Regex("""<w:hyperlink[^>]*r:id="(rId\d+)"""")
            .find(docXml)?.groupValues?.get(1)
            ?: error("expected <w:hyperlink r:id=...> in patched document.xml")
        assertTrue(
            """Id="$rid"""" in relsXml &&
                """Target="https://example.com"""" in relsXml &&
                """TargetMode="External"""" in relsXml,
            "rels missing hyperlink entry: $relsXml",
        )
    }

    @Test fun `paragraph snippet with image resolves rId, writes binary, adds ContentType`() {
        val pngBytes = TINY_PNG
        val input = buildSimpleDocx("a {{block}} b")
        val output = PatchDocument.patch(
            input,
            mapOf(
                "block" to Patch.Paragraphs(
                    paragraphs {
                        paragraph {
                            image(
                                bytes = pngBytes,
                                widthEmus = 952_500,
                                heightEmus = 952_500,
                                format = ImageFormat.PNG,
                            )
                        }
                    }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        val relsXml = parts.getValue("word/_rels/document.xml.rels").toString(Charsets.UTF_8)
        val ctXml = parts.getValue("[Content_Types].xml").toString(Charsets.UTF_8)

        val rid = Regex("""<a:blip[^/]*r:embed="(rId\d+)"""")
            .find(docXml)?.groupValues?.get(1)
            ?: error("expected <a:blip r:embed=...> in patched document.xml")
        val target = Regex("""Id="$rid"[^/]*Target="(media/image\d+\.png)"""")
            .find(relsXml)?.groupValues?.get(1)
            ?: error("rels missing matching image rel for $rid: $relsXml")
        assertContentEquals(
            pngBytes,
            parts.getValue("word/$target"),
            "image binary mismatch at word/$target",
        )
        assertTrue(
            """Extension="png"""" in ctXml,
            "Content_Types missing png Default: $ctXml",
        )
    }

    @Test fun `top-level table snippet splices into body where marker was`() {
        val input = buildSimpleDocx("before {{tbl}} after")
        val output = PatchDocument.patch(
            input,
            mapOf(
                "tbl" to Patch.Paragraphs(
                    paragraphs {
                        table {
                            row {
                                cell { paragraph { text("A") } }
                                cell { paragraph { text("B") } }
                            }
                        }
                    }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        // The body now contains a <w:tbl>...</w:tbl>.
        assertTrue("<w:tbl>" in docXml, "expected <w:tbl> after Table snippet patch")
        assertTrue(">A<" in docXml, "expected cell A in patched document")
        assertTrue(">B<" in docXml, "expected cell B in patched document")
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

    private companion object {
        val TINY_PNG = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A,
            0x00, 0x00, 0x00, 0x0D,
            0x49, 0x48, 0x44, 0x52,
            0x00, 0x00, 0x00, 0x01,
            0x00, 0x00, 0x00, 0x01,
            0x08, 0x06, 0x00, 0x00, 0x00,
            0x1F, 0x15.toByte(), 0xC4.toByte(), 0x89.toByte(),
            0x00, 0x00, 0x00, 0x0D,
            0x49, 0x44, 0x41, 0x54,
            0x78, 0x9C.toByte(),
            0x62, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x01,
            0x0D, 0x0A, 0x2D, 0xB4.toByte(),
            0x00, 0x00, 0x00, 0x00,
            0x49, 0x45, 0x4E, 0x44,
            0xAE.toByte(), 0x42, 0x60, 0x82.toByte(),
        )
    }
}

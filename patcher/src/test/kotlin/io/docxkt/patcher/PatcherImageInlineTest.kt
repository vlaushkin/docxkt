// Synthetic test: ImageRun inside a Patch.ParagraphInline patch.
// Verifies the patcher allocates a per-part image rId, registers a
// `.../image` rel,
// emits `<w:drawing>...<a:blip r:embed="rIdN"/>...`, and
// queues the binary at `word/media/image{N}.{ext}` plus a
// `<Default Extension>` entry on `[Content_Types].xml`.
package io.docxkt.patcher

import io.docxkt.pack.toByteArray
import io.docxkt.api.runs
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.io.DocxReader
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertTrue

internal class PatcherImageInlineTest {

    @Test fun `image in inline patch — allocates rId, writes image rel + binary + content-type`() {
        val pngBytes = TINY_PNG
        val input = buildSimpleDocx("Look here: {{img}} done.")
        val output = PatchDocument.patch(
            input,
            mapOf(
                "img" to Patch.ParagraphInline(
                    runs {
                        image(
                            bytes = pngBytes,
                            widthEmus = 952_500,
                            heightEmus = 952_500,
                            format = ImageFormat.PNG,
                        )
                    }
                ),
            ),
        )
        val parts = DocxReader.read(output)
        val docXml = parts.getValue("word/document.xml").toString(Charsets.UTF_8)
        val relsXml = parts.getValue("word/_rels/document.xml.rels").toString(Charsets.UTF_8)
        val ctXml = parts.getValue("[Content_Types].xml").toString(Charsets.UTF_8)

        // The drawing in document.xml carries an r:embed.
        val embedRid = Regex("""<a:blip[^/]*r:embed="(rId\d+)"""")
            .find(docXml)?.groupValues?.get(1)
            ?: error("expected <a:blip r:embed=...> in patched document.xml. Got: $docXml")

        // The rels file has a Relationship of type .../image with
        // matching Id and Target pointing at media/image{N}.png.
        assertTrue(
            """Id="$embedRid"""" in relsXml,
            "rels missing Id=\"$embedRid\". Got: $relsXml",
        )
        assertTrue(
            """Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"""" in relsXml,
            "rels missing image Type. Got: $relsXml",
        )
        val targetMatch = Regex("""Target="(media/image\d+\.png)"""").find(relsXml)
            ?: error("rels missing image Target. Got: $relsXml")
        val mediaPath = "word/${targetMatch.groupValues[1]}"

        // The media binary lives at the queued path with the
        // exact bytes we passed in.
        assertContentEquals(
            pngBytes,
            parts.getValue(mediaPath),
            "media binary at $mediaPath does not match input bytes",
        )

        // [Content_Types].xml carries a Default for the .png
        // extension (added once even if multiple images use it).
        assertTrue(
            """Extension="png"""" in ctXml,
            "Content_Types missing Default Extension=png. Got: $ctXml",
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

    private companion object {
        // Tiny 1x1 transparent PNG; we don't validate, only embed.
        val TINY_PNG = byteArrayOf(
            0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, // signature
            0x00, 0x00, 0x00, 0x0D, // IHDR length
            0x49, 0x48, 0x44, 0x52, // "IHDR"
            0x00, 0x00, 0x00, 0x01, // width = 1
            0x00, 0x00, 0x00, 0x01, // height = 1
            0x08, 0x06, 0x00, 0x00, 0x00, // 8-bit RGBA
            0x1F, 0x15.toByte(), 0xC4.toByte(), 0x89.toByte(), // CRC
            0x00, 0x00, 0x00, 0x0D, // IDAT length
            0x49, 0x44, 0x41, 0x54, // "IDAT"
            0x78, 0x9C.toByte(),
            0x62, 0x00, 0x01, 0x00, 0x00, 0x05, 0x00, 0x01,
            0x0D, 0x0A, 0x2D, 0xB4.toByte(),
            0x00, 0x00, 0x00, 0x00, // IEND length
            0x49, 0x45, 0x4E, 0x44, // "IEND"
            0xAE.toByte(), 0x42, 0x60, 0x82.toByte(), // CRC
        )
    }
}

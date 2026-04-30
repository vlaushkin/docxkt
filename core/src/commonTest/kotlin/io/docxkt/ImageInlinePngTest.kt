package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class ImageInlinePngTest : DocxFixtureTest("image-inline-png") {

    override fun build(): Document = document {
        paragraph {
            image(
                bytes = loadFixtureBytes("image-inline-png", "word/media/image1.png"),
                widthEmus = 952_500,   // 100 px at 96 DPI = 100 * 9525 EMU
                heightEmus = 952_500,
                format = ImageFormat.PNG,
            )
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
    override val comparedBinaryParts: List<String> = listOf("word/media/image1.png")
}

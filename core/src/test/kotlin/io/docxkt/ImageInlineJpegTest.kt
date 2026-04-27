package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.FixtureLoader

internal class ImageInlineJpegTest : DocxFixtureTest("image-inline-jpeg") {

    override fun build(): Document = document {
        paragraph {
            image(
                bytes = FixtureLoader.loadBinaryPart(
                    fixtureName = "image-inline-jpeg",
                    partPath = "word/media/image1.jpg",
                ),
                widthEmus = 952_500,
                heightEmus = 952_500,
                format = ImageFormat.JPEG,
            )
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
    override val comparedBinaryParts: List<String> = listOf("word/media/image1.jpg")
}

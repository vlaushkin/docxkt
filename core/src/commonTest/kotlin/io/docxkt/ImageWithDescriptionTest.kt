package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class ImageWithDescriptionTest : DocxFixtureTest("image-with-description") {

    override fun build(): Document = document {
        paragraph {
            image(
                bytes = loadFixtureBytes("image-with-description", "word/media/image1.png"),
                widthEmus = 952_500,
                heightEmus = 952_500,
                format = ImageFormat.PNG,
                description = "a tiny red square",
            )
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
    override val comparedBinaryParts: List<String> = listOf("word/media/image1.png")
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.FixtureLoader

internal class ImageAfterHeaderAndFooterTest :
    DocxFixtureTest("image-after-header-and-footer") {

    override fun build(): Document = document {
        header { paragraph { text("hdr") } }
        footer { paragraph { text("ftr") } }
        paragraph {
            image(
                bytes = FixtureLoader.loadBinaryPart(
                    fixtureName = "image-after-header-and-footer",
                    partPath = "word/media/image1.png",
                ),
                widthEmus = 952_500,
                heightEmus = 952_500,
                format = ImageFormat.PNG,
            )
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/footer1.xml",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.png",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.HorizontalRelativeFrom
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.drawing.VerticalRelativeFrom
import io.docxkt.testing.DocxFixtureTest
import java.io.File

internal class AnchorNoneWrapTest : DocxFixtureTest("anchor-none-wrap") {

    override fun build(): Document = document {
        val png = File(
            javaClass.classLoader.getResource(
                "fixtures/anchor-none-wrap/word/media/image1.png"
            )!!.toURI()
        ).readBytes()

        paragraph {
            imageAnchor(
                bytes = png,
                widthEmus = 1_905_000,
                heightEmus = 952_500,
                format = ImageFormat.PNG,
            ) {
                positionH(relativeFrom = HorizontalRelativeFrom.COLUMN, offsetEmus = 0)
                positionV(relativeFrom = VerticalRelativeFrom.PARAGRAPH, offsetEmus = 0)
                wrapNone()
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )

    override val comparedBinaryParts: List<String> = listOf(
        "word/media/image1.png",
    )
}

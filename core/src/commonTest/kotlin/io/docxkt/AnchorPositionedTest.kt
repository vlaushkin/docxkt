package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.HorizontalAlign
import io.docxkt.model.drawing.HorizontalRelativeFrom
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.drawing.VerticalRelativeFrom
import io.docxkt.model.drawing.WrapSide
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureBytes

internal class AnchorPositionedTest : DocxFixtureTest("anchor-positioned") {

    override fun build(): Document = document {
        val png = loadFixtureBytes("anchor-positioned", "word/media/image1.png")

        paragraph {
            imageAnchor(
                bytes = png,
                widthEmus = 1_905_000,
                heightEmus = 952_500,
                format = ImageFormat.PNG,
            ) {
                positionH(
                    relativeFrom = HorizontalRelativeFrom.MARGIN,
                    align = HorizontalAlign.CENTER,
                )
                positionV(
                    relativeFrom = VerticalRelativeFrom.PARAGRAPH,
                    offsetEmus = 914_400,
                )
                wrapSquare(side = WrapSide.BOTH_SIDES)
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

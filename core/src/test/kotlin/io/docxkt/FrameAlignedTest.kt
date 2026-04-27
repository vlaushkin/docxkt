package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.drawing.HorizontalAlign
import io.docxkt.model.drawing.VerticalAlign
import io.docxkt.model.paragraph.FrameAnchor
import io.docxkt.model.paragraph.FrameDropCap
import io.docxkt.testing.DocxFixtureTest

internal class FrameAlignedTest : DocxFixtureTest("frame-aligned") {

    override fun build(): Document = document {
        paragraph {
            framePr {
                widthTwips = 1440
                heightTwips = 1440
                positionAligned(xAlign = HorizontalAlign.LEFT, yAlign = VerticalAlign.TOP)
                hAnchor = FrameAnchor.TEXT
                vAnchor = FrameAnchor.TEXT
                dropCap = FrameDropCap.DROP
                lines = 3
            }
            text("Drop-cap framed")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.FrameAnchor
import io.docxkt.model.paragraph.FrameWrap
import io.docxkt.testing.DocxFixtureTest

internal class FramePositionedTest : DocxFixtureTest("frame-positioned") {

    override fun build(): Document = document {
        paragraph {
            framePr {
                widthTwips = 2880
                heightTwips = 1440
                positionXy(xTwips = 1440, yTwips = 1440)
                hAnchor = FrameAnchor.PAGE
                vAnchor = FrameAnchor.PAGE
                wrap = FrameWrap.AROUND
            }
            text("Framed paragraph")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

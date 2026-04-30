package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.run.HighlightColor
import io.docxkt.testing.DocxFixtureTest

internal class Demo45HighlightingTextTest : DocxFixtureTest("demo-45-highlighting-text") {

    override fun build(): Document = document {
        header {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("Hello World") {
                    color = "FF0000"
                    bold = true
                    size = 24
                    font("Garamond")
                    highlight = HighlightColor.YELLOW
                }
            }
        }
    }
}

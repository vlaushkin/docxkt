package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphBordersFullTest : DocxFixtureTest("paragraph-borders-full") {

    override fun build(): Document = document {
        paragraph {
            borders {
                top = BorderSide(style = BorderStyle.SINGLE, size = 6, color = "FF0000")
                bottom = BorderSide(style = BorderStyle.SINGLE, size = 6, color = "00FF00")
                left = BorderSide(style = BorderStyle.SINGLE, size = 6, color = "0000FF")
                right = BorderSide(style = BorderStyle.SINGLE, size = 6, color = "FFFF00")
                between = BorderSide(style = BorderStyle.SINGLE, size = 4, color = "auto")
            }
            text("bordered")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

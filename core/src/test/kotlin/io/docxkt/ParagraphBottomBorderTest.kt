package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphBottomBorderTest : DocxFixtureTest("paragraph-bottom-border") {

    override fun build(): Document = document {
        paragraph {
            borders {
                bottom = BorderSide(
                    style = BorderStyle.SINGLE,
                    size = 6,
                    color = "auto",
                    space = 1,
                )
            }
            text("hr")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

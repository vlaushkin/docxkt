package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class Demo26ParagraphBordersTest : DocxFixtureTest("demo-26-paragraph-borders") {

    override fun build(): Document = document {
        paragraph { text("No border!") }
        paragraph {
            borders {
                top = BorderSide(style = BorderStyle.SINGLE, color = "auto", size = 6, space = 1)
                bottom = BorderSide(style = BorderStyle.SINGLE, color = "auto", size = 6, space = 1)
            }
            text("I have borders on my top and bottom sides!")
        }
        paragraph {
            borders {
                top = BorderSide(style = BorderStyle.SINGLE, color = "auto", size = 6, space = 1)
            }
        }
        paragraph {
            text("This will ")
            text("have a border.") {
                border = BorderSide(style = BorderStyle.SINGLE, color = "auto", size = 6, space = 1)
            }
            text(" This will not.")
        }
    }
}

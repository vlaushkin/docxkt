package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class CellBordersTest : DocxFixtureTest("cell-borders") {

    override fun build(): Document = document {
        table {
            row {
                cell {
                    borders {
                        top = BorderSide(style = BorderStyle.THICK, size = 16, color = "00FF00")
                        bottom = BorderSide(style = BorderStyle.DASHED, size = 8, color = "0000FF")
                    }
                    paragraph { text("C") }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

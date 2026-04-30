package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class TableBordersFullTest : DocxFixtureTest("table-borders-full") {

    override fun build(): Document = document {
        table {
            val outer = BorderSide(style = BorderStyle.DOUBLE, size = 12, color = "3366FF")
            val inside = BorderSide(style = BorderStyle.DASHED, size = 8, color = "CCCCCC")
            borders {
                top = outer
                left = outer
                bottom = outer
                right = outer
                insideHorizontal = inside
                insideVertical = inside
            }
            row {
                cell { paragraph { text("A") } }
                cell { paragraph { text("B") } }
            }
            row {
                cell { paragraph { text("C") } }
                cell { paragraph { text("D") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

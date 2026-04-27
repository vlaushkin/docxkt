package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.testing.DocxFixtureTest

internal class TableBordersPartialTest : DocxFixtureTest("table-borders-partial") {

    override fun build(): Document = document {
        table {
            val red = BorderSide(style = BorderStyle.THICK, size = 16, color = "FF0000")
            borders {
                top = red
                bottom = red
            }
            row {
                cell { paragraph { text("P") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

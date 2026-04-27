package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class CellMarginsTableTest : DocxFixtureTest("cell-margins-table") {

    override fun build(): Document = document {
        table {
            cellMargins(top = 100, left = 120, bottom = 100, right = 120)
            row {
                cell { paragraph { text("M") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

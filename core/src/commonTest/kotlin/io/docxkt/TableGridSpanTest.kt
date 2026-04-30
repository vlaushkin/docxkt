package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class TableGridSpanTest : DocxFixtureTest("table-grid-span") {

    override fun build(): Document = document {
        table {
            columnWidths(1500, 1500)
            row {
                cell {
                    gridSpan(2)
                    paragraph { text("Spanned") }
                }
            }
            row {
                cell { paragraph { text("L") } }
                cell { paragraph { text("R") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

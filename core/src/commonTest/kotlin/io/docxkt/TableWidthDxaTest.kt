package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class TableWidthDxaTest : DocxFixtureTest("table-width-dxa") {

    override fun build(): Document = document {
        table {
            width(TableWidth.dxa(5000))
            columnWidths(2500, 2500)
            row {
                cell {
                    width(TableWidth.dxa(2500))
                    paragraph { text("L") }
                }
                cell {
                    width(TableWidth.dxa(2500))
                    paragraph { text("R") }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

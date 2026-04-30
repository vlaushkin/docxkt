package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class Demo100TableLookTest : DocxFixtureTest("demo-100-table-look") {

    private fun rows3(headers: List<String>, body: List<List<String>>): io.docxkt.api.Document = document {} // unused stub

    override fun build(): Document = document {
        paragraph {
            text("Table 1: Table Look Default Values") { bold = true }
        }
        paragraph {}
        table {
            styleReference = "MyCustomTableStyle"
            width(TableWidth.pct(100))
            row {
                cell { paragraph { text("Header 1") } }
                cell { paragraph { text("Header 2") } }
                cell { paragraph { text("Header 3") } }
            }
            row {
                cell { paragraph { text("Row 1, Col 1") } }
                cell { paragraph { text("Row 1, Col 2") } }
                cell { paragraph { text("Row 1, Col 3") } }
            }
            row {
                cell { paragraph { text("Row 2, Col 1") } }
                cell { paragraph { text("Row 2, Col 2") } }
                cell { paragraph { text("Row 2, Col 3") } }
            }
            row {
                cell { paragraph { text("Row 3, Col 1") } }
                cell { paragraph { text("Row 3, Col 2") } }
                cell { paragraph { text("Row 3, Col 3") } }
            }
        }
        paragraph {}
        paragraph {}
        paragraph {
            text("Table 2: Table Look All Look Values Enabled") { bold = true }
        }
        paragraph {}
        table {
            styleReference = "MyCustomTableStyle"
            width(TableWidth.pct(100))
            tableLook(
                firstRow = true,
                lastRow = true,
                firstColumn = true,
                lastColumn = true,
                noHBand = false,
                noVBand = false,
            )
            row {
                cell { paragraph { text("Header 1") } }
                cell { paragraph { text("Header 2") } }
                cell { paragraph { text("Header 3") } }
            }
            row {
                cell { paragraph { text("Row 1, Col 1") } }
                cell { paragraph { text("Row 1, Col 2") } }
                cell { paragraph { text("Row 1, Col 3") } }
            }
            row {
                cell { paragraph { text("Row 2, Col 1") } }
                cell { paragraph { text("Row 2, Col 2") } }
                cell { paragraph { text("Row 2, Col 3") } }
            }
            row {
                cell { paragraph { text("Row 3, Col 1") } }
                cell { paragraph { text("Row 3, Col 2") } }
                cell { paragraph { text("Row 3, Col 3") } }
            }
        }
    }
}

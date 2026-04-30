package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class Demo4BasicTableTest : DocxFixtureTest("demo-4-basic-table") {

    override fun build(): Document = document {
        paragraph { text("Table with skewed widths") }
        table {
            columnWidths(3505, 5505)
            row {
                cell {
                    width(TableWidth.dxa(3505))
                    paragraph { text("Hello") }
                }
                cell { width(TableWidth.dxa(5505)) }
            }
            row {
                cell { width(TableWidth.dxa(3505)) }
                cell {
                    width(TableWidth.dxa(5505))
                    paragraph { text("World") }
                }
            }
        }
        paragraph { text("Table with equal widths") }
        table {
            columnWidths(4505, 4505)
            row {
                cell {
                    width(TableWidth.dxa(4505))
                    paragraph { text("Hello") }
                }
                cell { width(TableWidth.dxa(4505)) }
            }
            row {
                cell { width(TableWidth.dxa(4505)) }
                cell {
                    width(TableWidth.dxa(4505))
                    paragraph { text("World") }
                }
            }
        }
        paragraph { text("Table without setting widths") }
        table {
            row {
                cell { paragraph { text("Hello") } }
                cell { }
            }
            row {
                cell { }
                cell { paragraph { text("World") } }
            }
        }
    }
}

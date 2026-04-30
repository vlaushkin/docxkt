package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.dsl.TableScope
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class Demo77SideBySideTablesTest : DocxFixtureTest("demo-77-side-by-side-tables") {

    private fun TableScope.helloWorldInner() {
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

    private fun TableScope.fooBarInner() {
        columnWidths(3505, 5505)
        row {
            cell {
                width(TableWidth.dxa(3505))
                paragraph { text("Foo") }
            }
            cell { width(TableWidth.dxa(5505)) }
        }
        row {
            cell { width(TableWidth.dxa(3505)) }
            cell {
                width(TableWidth.dxa(5505))
                paragraph { text("Bar") }
            }
        }
    }

    override fun build(): Document = document {
        table {
            bordersAllNone()
            row {
                cell {
                    table { helloWorldInner() }
                }
                cell {
                    table { fooBarInner() }
                }
            }
        }
    }
}

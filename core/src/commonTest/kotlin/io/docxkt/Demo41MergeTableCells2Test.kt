package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.dsl.TableScope
import io.docxkt.model.table.VerticalMerge
import io.docxkt.testing.DocxFixtureTest

internal class Demo41MergeTableCells2Test : DocxFixtureTest("demo-41-merge-table-cells-2") {

    override fun build(): Document = document {
        // Table 1 — 6-col grid, gridSpan=2 cells across rows 0/1/2,
        // single span-5 cell in row 4.
        table {
            columnWidths(100, 100, 100, 100, 100, 100)
            row {
                cell { paragraph { text("0,0") } }
                cell { gridSpan(2); paragraph { text("0,1") } }
                cell { paragraph { text("0,3") } }
                cell { gridSpan(2); paragraph { text("0,4") } }
            }
            row {
                cell { gridSpan(2); paragraph { text("1,0") } }
                cell { gridSpan(2); paragraph { text("1,2") } }
                cell { gridSpan(2); paragraph { text("1,4") } }
            }
            row {
                cell { paragraph { text("2,0") } }
                cell { gridSpan(2); paragraph { text("2,1") } }
                cell { paragraph { text("2,3") } }
                cell { gridSpan(2); paragraph { text("2,4") } }
            }
            row {
                cell { paragraph { text("3,0") } }
                cell { paragraph { text("3,1") } }
                cell { paragraph { text("3,2") } }
                cell { paragraph { text("3,3") } }
                cell { paragraph { text("3,4") } }
                cell { paragraph { text("3,5") } }
            }
            row {
                cell { gridSpan(5); paragraph { text("4,0") } }
                cell { paragraph { text("4,5") } }
            }
            row {
                repeat(6) { cell {} }
            }
        }
        paragraph { text("") }
        // Table 2 — rowSpan=2 on row 0 col 1; row 1 gets the
        // auto-injected vMerge=continue placeholder + the 5 source
        // cells from the demo.
        table {
            columnWidths(100, 100, 100, 100, 100, 100)
            row {
                cell { paragraph { text("0,0") } }
                cell { verticalMerge(VerticalMerge.RESTART); paragraph { text("0,1") } }
                cell { paragraph { text("0,2") } }
                cell { paragraph { text("0,3") } }
                cell { paragraph { text("0,4") } }
                cell { paragraph { text("0,5") } }
            }
            row {
                cell { paragraph { text("1,0") } }
                cell { verticalMerge(VerticalMerge.CONTINUE) }
                cell { paragraph { text("1,2") } }
                cell { paragraph { text("1,3") } }
                cell { paragraph { text("1,4") } }
                cell { paragraph { text("1,5") } }
            }
            for (r in 2..4) {
                row {
                    for (c in 0..5) {
                        cell { paragraph { text("$r,$c") } }
                    }
                }
            }
            row {
                repeat(6) { cell {} }
            }
        }
    }
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.VerticalMerge
import io.docxkt.testing.DocxFixtureTest

internal class TableVMergeTest : DocxFixtureTest("table-vmerge") {

    override fun build(): Document = document {
        table {
            columnWidths(1500, 1500)
            row {
                cell {
                    verticalMerge(VerticalMerge.RESTART)
                    paragraph { text("Tall") }
                }
                cell { paragraph { text("R1") } }
            }
            row {
                cell {
                    verticalMerge(VerticalMerge.CONTINUE)
                    // No paragraphs — DSL auto-pads an empty <w:p/>.
                }
                cell { paragraph { text("R2") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

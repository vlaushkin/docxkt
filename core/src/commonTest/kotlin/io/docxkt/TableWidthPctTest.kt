package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TableWidth
import io.docxkt.testing.DocxFixtureTest

internal class TableWidthPctTest : DocxFixtureTest("table-width-pct") {

    override fun build(): Document = document {
        table {
            width(TableWidth.pct(5000))
            row {
                cell { paragraph { text("P") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

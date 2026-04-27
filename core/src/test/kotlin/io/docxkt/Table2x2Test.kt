package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Table2x2Test : DocxFixtureTest("table-2x2") {

    override fun build(): Document = document {
        table {
            row {
                cell { paragraph { text("A1") } }
                cell { paragraph { text("B1") } }
            }
            row {
                cell { paragraph { text("A2") } }
                cell { paragraph { text("B2") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

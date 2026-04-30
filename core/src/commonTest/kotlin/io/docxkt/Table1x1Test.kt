package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Table1x1Test : DocxFixtureTest("table-1x1") {

    override fun build(): Document = document {
        table {
            row {
                cell {
                    paragraph { text("A") }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.HeightRule
import io.docxkt.testing.DocxFixtureTest

internal class TableHeaderHeightTest : DocxFixtureTest("table-header-height") {

    override fun build(): Document = document {
        table {
            row {
                tableHeader = true
                height(twips = 500, rule = HeightRule.ATLEAST)
                cell { paragraph { text("Header") } }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

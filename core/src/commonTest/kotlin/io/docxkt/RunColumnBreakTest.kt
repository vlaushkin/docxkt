package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunColumnBreakTest : DocxFixtureTest("run-column-break") {

    override fun build(): Document = document {
        paragraph {
            text("col1")
            columnBreak()
            text("col2")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

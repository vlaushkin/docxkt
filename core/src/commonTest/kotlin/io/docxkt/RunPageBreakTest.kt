package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunPageBreakTest : DocxFixtureTest("run-page-break") {

    override fun build(): Document = document {
        paragraph {
            text("before")
            pageBreak()
            text("after")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

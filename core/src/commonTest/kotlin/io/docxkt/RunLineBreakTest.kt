package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunLineBreakTest : DocxFixtureTest("run-line-break") {

    override fun build(): Document = document {
        paragraph {
            text("line1")
            lineBreak()
            text("line2")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

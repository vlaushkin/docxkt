package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunHyphensTest : DocxFixtureTest("run-hyphens") {

    override fun build(): Document = document {
        paragraph {
            text("co")
            softHyphen()
            text("operate")
            text(" ")
            text("up")
            noBreakHyphen()
            text("to")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

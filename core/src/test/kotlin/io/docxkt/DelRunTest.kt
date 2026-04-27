package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class DelRunTest : DocxFixtureTest("del-run") {

    override fun build(): Document = document {
        paragraph {
            text("Kept. ")
            deletedText(author = "A", date = "2026-04-24T00:00:00Z", id = 1) {
                text("removed")
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

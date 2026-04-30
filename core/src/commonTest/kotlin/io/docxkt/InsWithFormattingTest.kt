package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class InsWithFormattingTest : DocxFixtureTest("ins-with-formatting") {

    override fun build(): Document = document {
        paragraph {
            text("Kept. ")
            insertedText(author = "A", date = "2026-04-24T00:00:00Z", id = 1) {
                text("bold addition") { bold = true }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

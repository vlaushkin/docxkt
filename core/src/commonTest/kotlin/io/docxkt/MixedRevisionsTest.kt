package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MixedRevisionsTest : DocxFixtureTest("mixed-revisions") {

    override fun build(): Document = document {
        paragraph {
            text("Before ")
            insertedText(author = "A", date = "2026-04-24T00:00:00Z") {
                text("plus")
            }
            text(" ")
            deletedText(author = "A", date = "2026-04-24T00:00:00Z") {
                text("minus")
            }
            text(" after")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class CommentSpanningRunsTest : DocxFixtureTest("comment-spanning-runs") {

    override fun build(): Document = document {
        comment(
            id = 0,
            author = "Bob",
            date = "2026-04-24T00:00:00.000Z",
        ) {
            paragraph { text("Applies to both bold and plain.") }
        }
        paragraph {
            commentRangeStart(0)
            text("bold") { bold = true }
            text(" and plain")
            commentRangeEnd(0)
            commentReference(0)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/comments.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class CommentMultiAuthorTest : DocxFixtureTest("comment-multi-author") {

    override fun build(): Document = document {
        comment(
            id = 0, author = "Alice", initials = "AB",
            date = "2026-04-24T00:00:00.000Z",
        ) {
            paragraph { text("Alice's note.") }
        }
        comment(
            id = 1, author = "Bob", initials = "BM",
            date = "2026-04-24T00:00:00.000Z",
        ) {
            paragraph { text("Bob's note.") }
        }
        paragraph {
            commentRangeStart(0)
            text("first span")
            commentRangeEnd(0)
            commentReference(0)
        }
        paragraph {
            commentRangeStart(1)
            text("second span")
            commentRangeEnd(1)
            commentReference(1)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/comments.xml",
    )
}

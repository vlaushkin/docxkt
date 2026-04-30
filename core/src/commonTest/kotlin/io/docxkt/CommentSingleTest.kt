package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class CommentSingleTest : DocxFixtureTest("comment-single") {

    override fun build(): Document = document {
        comment(
            id = 0,
            author = "Alice",
            initials = "AB",
            date = "2026-04-24T00:00:00.000Z",
        ) {
            paragraph { text("A remark.") }
        }
        paragraph {
            commentRangeStart(0)
            text("spanned text")
            commentRangeEnd(0)
            commentReference(0)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/comments.xml",
    )
}

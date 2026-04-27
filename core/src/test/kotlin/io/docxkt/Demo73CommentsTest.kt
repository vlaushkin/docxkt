package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo73CommentsTest : DocxFixtureTest("demo-73-comments") {

    private val sentinel = "2026-04-24T00:00:00.000Z"

    override fun build(): Document = document {
        // Comments live in word/comments.xml (not compared).
        // Stand-in bodies so the IDs resolve.
        comment(0, author = "Ray Chen", date = sentinel) {
            paragraph { text("comment 0") }
        }
        comment(1, author = "Bob Ross", date = sentinel) {
            paragraph { text("comment 1") }
        }
        comment(2, author = "John Doe", date = sentinel) {
            paragraph { text("comment 2") }
        }
        comment(3, author = "Beatriz", date = sentinel) {
            paragraph { text("comment 3") }
        }

        paragraph {
            text("Hello World")
            commentRangeStart(0)
            text("Foo Bar") { bold = true }
            commentRangeEnd(0)
            commentReference(0) { bold = true }
        }
        paragraph {
            commentRangeStart(1)
            commentRangeStart(2)
            commentRangeStart(3)
            text("Some text which need commenting") { bold = true }
            commentRangeEnd(1)
            commentReference(1) { bold = true }
            commentRangeEnd(2)
            commentReference(2) { bold = true }
            commentRangeEnd(3)
            commentReference(3) { bold = true }
        }
    }
}

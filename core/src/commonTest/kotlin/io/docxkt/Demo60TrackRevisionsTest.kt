package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class Demo60TrackRevisionsTest : DocxFixtureTest("demo-60-track-revisions") {

    override fun build(): Document = document {
        settings { trackRevisions = true }

        footnote(1) {
            paragraph {
                text("This is a footnote")
                deletedText(
                    author = AUTHOR,
                    date = NOTE_DATE,
                    id = 0,
                ) {
                    text(" with some extra text which was deleted")
                }
                insertedText(
                    author = AUTHOR,
                    date = NOTE_DATE,
                    id = 1,
                ) {
                    text(" and new content")
                }
            }
        }

        footer {
            paragraph {
                alignment = AlignmentType.CENTER
                text("Awesome LLC")
                text("Page Number: ") { pageNumber() }
                deletedText(author = AUTHOR, date = NOTE_DATE, id = 4) {
                    text(" to ") { totalPages() }
                }
                insertedText(author = AUTHOR, date = NOTE_DATE, id = 5) {
                    text(" from ") {
                        bold = true
                        totalPages()
                    }
                }
            }
        }

        paragraph {
            text("This is a simple demo ")
            text("on how to ")
            insertedText(author = AUTHOR, date = BODY_DATE, id = 0) {
                text("mark a text as an insertion ")
            }
            deletedText(author = AUTHOR, date = BODY_DATE, id = 1) {
                text("or a deletion.")
            }
        }

        paragraph {
            text("This is a demo ")
            deletedText(author = AUTHOR, date = BODY_DATE, id = 2) {
                text("in order") {
                    leadingLineBreak()
                    bold = true
                    size = 24
                    color = "ff0000"
                    font("Garamond")
                    shading(
                        ShadingPattern.REVERSE_DIAG_STRIPE,
                        color = "00FFFF",
                        fill = "FF0000",
                    )
                }
            }
            insertedText(author = AUTHOR, date = BODY_DATE, id = 3) {
                text("to show how to ") { bold = false }
            }
            text("use Inserted and Deleted TextRuns.") {
                bold = true
                tab()
                footnoteReference(1)
            }
            text("And some style changes") {
                bold = true
                revision(
                    id = 4,
                    author = AUTHOR,
                    date = NOTE_DATE,
                ) { bold = false }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/footer1.xml",
        "word/footnotes.xml",
        "word/settings.xml",
    )

    private companion object {
        const val AUTHOR = "Firstname Lastname"
        // Body ins/del dates are sentinel-stripped by extract-demo.mjs
        // (only word/document.xml and word/comments.xml are stripped);
        // footer/footnote ins/del + rPrChange retain the upstream value.
        const val BODY_DATE = "2026-04-24T00:00:00.000Z"
        const val NOTE_DATE = "2020-10-06T09:05:00Z"
    }
}

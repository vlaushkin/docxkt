package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class BothNotesInOneDocumentTest : DocxFixtureTest("both-notes-in-one-document") {

    override fun build(): Document = document {
        footnote(id = 1) {
            paragraph { text("Footnote.") }
        }
        endnote(id = 1) {
            paragraph { text("Endnote.") }
        }
        paragraph {
            text("Claim")
            footnoteReference(1)
        }
        paragraph {
            text("Aside")
            endnoteReference(1)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/footnotes.xml",
        "word/endnotes.xml",
    )
}

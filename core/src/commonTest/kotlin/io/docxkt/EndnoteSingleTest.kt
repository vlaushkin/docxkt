package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class EndnoteSingleTest : DocxFixtureTest("endnote-single") {

    override fun build(): Document = document {
        endnote(id = 1) {
            paragraph { text("End note.") }
        }
        paragraph {
            text("See")
            endnoteReference(1)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/endnotes.xml",
    )
}

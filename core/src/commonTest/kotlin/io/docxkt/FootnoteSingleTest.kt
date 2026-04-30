package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FootnoteSingleTest : DocxFixtureTest("footnote-single") {

    override fun build(): Document = document {
        footnote(id = 1) {
            paragraph { text("First note.") }
        }
        paragraph {
            text("Claim")
            footnoteReference(1)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/footnotes.xml",
    )
}

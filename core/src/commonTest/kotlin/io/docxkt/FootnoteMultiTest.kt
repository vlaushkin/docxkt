package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FootnoteMultiTest : DocxFixtureTest("footnote-multi") {

    override fun build(): Document = document {
        footnote(id = 1) {
            paragraph { text("Note one.") }
        }
        footnote(id = 2) {
            paragraph { text("Note two.") }
        }
        paragraph {
            text("First")
            footnoteReference(1)
        }
        paragraph {
            text("Second")
            footnoteReference(2)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/footnotes.xml",
    )
}

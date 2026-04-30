package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class BookmarkWithHyperlinkTest : DocxFixtureTest("bookmark-with-hyperlink") {

    override fun build(): Document = document {
        paragraph {
            bookmark(name = "target") {
                text("Target heading")
            }
        }
        paragraph {
            text("See ")
            internalHyperlink(anchor = "target") {
                text("the target")
            }
            text(" above.")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

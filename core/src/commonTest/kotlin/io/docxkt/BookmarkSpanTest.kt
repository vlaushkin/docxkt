package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class BookmarkSpanTest : DocxFixtureTest("bookmark-span") {

    override fun build(): Document = document {
        paragraph {
            bookmarkStart("section")
            text("Start of section")
        }
        paragraph {
            text("End of section")
            bookmarkEnd("section")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

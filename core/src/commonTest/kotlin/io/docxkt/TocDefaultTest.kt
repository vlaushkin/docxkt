package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class TocDefaultTest : DocxFixtureTest("toc-default") {

    override fun build(): Document = document {
        tableOfContents {
            alias = "Contents"
            hyperlink = true
            headingStyleRange = "1-3"
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

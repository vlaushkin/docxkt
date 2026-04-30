package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class TocLevels13Test : DocxFixtureTest("toc-levels-1-3") {

    override fun build(): Document = document {
        tableOfContents {
            alias = "Contents"
            headingStyleRange = "1-3"
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

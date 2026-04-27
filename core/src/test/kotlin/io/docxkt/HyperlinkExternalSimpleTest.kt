package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HyperlinkExternalSimpleTest : DocxFixtureTest("hyperlink-external-simple") {

    override fun build(): Document = document {
        paragraph {
            text("Visit ")
            hyperlink("https://example.com") {
                text("example.com")
            }
            text(" for more.")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

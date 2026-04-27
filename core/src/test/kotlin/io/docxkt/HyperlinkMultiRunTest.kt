package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HyperlinkMultiRunTest : DocxFixtureTest("hyperlink-multi-run") {

    override fun build(): Document = document {
        paragraph {
            hyperlink("https://example.com") {
                text("bold ") { bold = true }
                text("plain")
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HeaderMultiParagraphTest : DocxFixtureTest("header-multi-paragraph") {

    override fun build(): Document = document {
        header {
            paragraph { text("Title") }
            paragraph { text("Subtitle") }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
    )
}

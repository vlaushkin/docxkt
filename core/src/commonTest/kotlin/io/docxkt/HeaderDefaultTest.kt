package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HeaderDefaultTest : DocxFixtureTest("header-default") {

    override fun build(): Document = document {
        header {
            paragraph { text("header text") }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
    )
}

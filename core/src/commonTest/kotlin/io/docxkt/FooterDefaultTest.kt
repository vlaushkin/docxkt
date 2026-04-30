package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FooterDefaultTest : DocxFixtureTest("footer-default") {

    override fun build(): Document = document {
        footer {
            paragraph { text("footer text") }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/footer1.xml",
    )
}

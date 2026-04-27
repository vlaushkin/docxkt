package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class HeaderAndFooterTest : DocxFixtureTest("header-and-footer") {

    override fun build(): Document = document {
        header { paragraph { text("hdr") } }
        footer { paragraph { text("ftr") } }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/footer1.xml",
    )
}

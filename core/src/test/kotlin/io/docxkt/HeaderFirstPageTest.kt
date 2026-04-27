package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.testing.DocxFixtureTest

internal class HeaderFirstPageTest : DocxFixtureTest("header-first-page") {

    override fun build(): Document = document {
        titlePage()
        header { paragraph { text("Default header") } }
        header(type = HeaderFooterReferenceType.FIRST) {
            paragraph { text("First-page header") }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/header2.xml",
    )
}

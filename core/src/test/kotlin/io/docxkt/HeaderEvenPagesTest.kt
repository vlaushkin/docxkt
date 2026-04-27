package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.testing.DocxFixtureTest

internal class HeaderEvenPagesTest : DocxFixtureTest("header-even-pages") {

    override fun build(): Document = document {
        header { paragraph { text("Default header") } }
        header(type = HeaderFooterReferenceType.EVEN) {
            paragraph { text("Even-page header") }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/header2.xml",
        "word/settings.xml",
    )
}

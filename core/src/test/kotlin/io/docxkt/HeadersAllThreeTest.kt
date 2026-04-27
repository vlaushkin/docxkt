package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.testing.DocxFixtureTest

internal class HeadersAllThreeTest : DocxFixtureTest("headers-all-three") {

    override fun build(): Document = document {
        titlePage()
        header { paragraph { text("Default") } }
        header(type = HeaderFooterReferenceType.FIRST) { paragraph { text("First") } }
        header(type = HeaderFooterReferenceType.EVEN) { paragraph { text("Even") } }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
        "word/header2.xml",
        "word/header3.xml",
        "word/settings.xml",
    )
}

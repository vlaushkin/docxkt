package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SectionCustomMarginsTest : DocxFixtureTest("section-custom-margins") {

    override fun build(): Document = document {
        margins(
            top = 720, right = 1000, bottom = 720, left = 1000,
            header = 360, footer = 360, gutter = 0,
        )
        paragraph { text("margins") }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

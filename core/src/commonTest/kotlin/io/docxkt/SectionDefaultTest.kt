package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SectionDefaultTest : DocxFixtureTest("section-default") {

    override fun build(): Document = document {
        paragraph { text("default") }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

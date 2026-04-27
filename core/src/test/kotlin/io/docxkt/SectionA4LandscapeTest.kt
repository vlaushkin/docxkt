package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SectionA4LandscapeTest : DocxFixtureTest("section-a4-landscape") {

    override fun build(): Document = document {
        a4Landscape()
        paragraph { text("landscape") }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

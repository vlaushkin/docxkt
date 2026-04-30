package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MultiSectionTest : DocxFixtureTest("multi-section") {

    override fun build(): Document = document {
        paragraph { text("Section 1 portrait") }
        sectionBreak { a4Portrait() }
        paragraph { text("Section 2 landscape") }
        a4Landscape()
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

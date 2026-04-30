package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SectionTwoColumnsEqualTest : DocxFixtureTest("section-two-columns-equal") {

    override fun build(): Document = document {
        columns(count = 2, spaceTwips = 720, equalWidth = true)
        paragraph { text("two columns") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

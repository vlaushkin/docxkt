package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SectionColumnsWithSeparatorTest : DocxFixtureTest("section-columns-with-separator") {

    override fun build(): Document = document {
        columns(count = 2, spaceTwips = 720, equalWidth = true, separator = true)
        paragraph { text("with separator") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

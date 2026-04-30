package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.Column
import io.docxkt.testing.DocxFixtureTest

internal class SectionThreeColumnsCustomWidthsTest : DocxFixtureTest("section-three-columns-custom-widths") {

    override fun build(): Document = document {
        columns(
            count = 3,
            equalWidth = false,
            individual = listOf(
                Column(widthTwips = 3000, spaceTwips = 360),
                Column(widthTwips = 4000, spaceTwips = 360),
                Column(widthTwips = 2500),
            ),
        )
        paragraph { text("custom widths") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.LineNumberRestart
import io.docxkt.testing.DocxFixtureTest

internal class SectionLineNumberingTest : DocxFixtureTest("section-line-numbering") {

    override fun build(): Document = document {
        lineNumbering(
            countBy = 1,
            start = 1,
            distanceTwips = 720,
            restart = LineNumberRestart.CONTINUOUS,
        )
        paragraph { text("with line numbers") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

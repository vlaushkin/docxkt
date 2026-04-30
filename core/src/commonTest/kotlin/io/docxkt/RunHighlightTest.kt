package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.run.HighlightColor
import io.docxkt.testing.DocxFixtureTest

internal class RunHighlightTest : DocxFixtureTest("run-highlight") {

    override fun build(): Document = document {
        paragraph {
            text("Highlighted") {
                highlight = HighlightColor.YELLOW
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

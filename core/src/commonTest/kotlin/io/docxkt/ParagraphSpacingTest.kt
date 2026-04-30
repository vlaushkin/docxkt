package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.LineRule
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphSpacingTest : DocxFixtureTest("paragraph-spacing") {

    override fun build(): Document = document {
        paragraph {
            spacing(before = 120, after = 240, line = 360, lineRule = LineRule.AUTO)
            text("Spaced")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

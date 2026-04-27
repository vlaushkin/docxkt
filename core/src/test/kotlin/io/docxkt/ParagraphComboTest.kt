package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.LineRule
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphComboTest : DocxFixtureTest("paragraph-combo") {

    override fun build(): Document = document {
        paragraph {
            alignment = AlignmentType.JUSTIFIED
            indent(left = 720, firstLine = 360)
            spacing(before = 120, after = 120, line = 240, lineRule = LineRule.AUTO)
            keepNext = true
            text("Combo")
        }
        paragraph {
            text("Plain")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

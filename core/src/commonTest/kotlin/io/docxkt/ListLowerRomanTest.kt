package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class ListLowerRomanTest : DocxFixtureTest("list-lower-roman") {

    override fun build(): Document = document {
        listTemplate(reference = "roman") {
            level(0, LevelFormat.LOWER_ROMAN, text = "%1.",
                  indentLeft = 720, indentHanging = 360)
        }
        paragraph {
            numbering(reference = "roman", level = 0)
            text("first")
        }
        paragraph {
            numbering(reference = "roman", level = 0)
            text("second")
        }
        paragraph {
            numbering(reference = "roman", level = 0)
            text("third")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/numbering.xml",
    )
}

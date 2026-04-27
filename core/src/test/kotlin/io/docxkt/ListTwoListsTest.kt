package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class ListTwoListsTest : DocxFixtureTest("list-two-lists") {

    override fun build(): Document = document {
        listTemplate(reference = "decimal-list") {
            level(0, LevelFormat.DECIMAL, text = "%1.",
                  indentLeft = 720, indentHanging = 360)
        }
        listTemplate(reference = "bullet-list") {
            level(0, LevelFormat.BULLET, text = "●",
                  indentLeft = 720, indentHanging = 360)
        }
        paragraph {
            numbering(reference = "decimal-list", level = 0)
            text("num-one")
        }
        paragraph {
            numbering(reference = "decimal-list", level = 0)
            text("num-two")
        }
        paragraph {
            numbering(reference = "bullet-list", level = 0)
            text("bullet-one")
        }
        paragraph {
            numbering(reference = "bullet-list", level = 0)
            text("bullet-two")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/numbering.xml",
    )
}

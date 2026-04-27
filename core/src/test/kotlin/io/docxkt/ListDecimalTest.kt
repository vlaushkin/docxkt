package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class ListDecimalTest : DocxFixtureTest("list-decimal") {

    override fun build(): Document = document {
        listTemplate(reference = "my-list") {
            level(0, LevelFormat.DECIMAL, text = "%1.",
                  indentLeft = 720, indentHanging = 360)
            level(1, LevelFormat.DECIMAL, text = "%2.",
                  indentLeft = 1440, indentHanging = 360)
            level(2, LevelFormat.DECIMAL, text = "%3.",
                  indentLeft = 2160, indentHanging = 360)
        }
        paragraph {
            numbering(reference = "my-list", level = 0)
            text("alpha")
        }
        paragraph {
            numbering(reference = "my-list", level = 1)
            text("beta")
        }
        paragraph {
            numbering(reference = "my-list", level = 2)
            text("gamma")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/numbering.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class ListBulletTest : DocxFixtureTest("list-bullet") {

    override fun build(): Document = document {
        listTemplate(reference = "bullets") {
            level(0, LevelFormat.BULLET, text = "●",
                  indentLeft = 720, indentHanging = 360)
            level(1, LevelFormat.BULLET, text = "○",
                  indentLeft = 1440, indentHanging = 360)
        }
        paragraph {
            numbering(reference = "bullets", level = 0)
            text("one")
        }
        paragraph {
            numbering(reference = "bullets", level = 1)
            text("nested")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/numbering.xml",
    )
}

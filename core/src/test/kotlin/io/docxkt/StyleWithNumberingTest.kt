package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.testing.DocxFixtureTest

internal class StyleWithNumberingTest : DocxFixtureTest("style-with-numbering") {

    override fun build(): Document = document {
        listTemplate(reference = "my-bullets") {
            level(0, LevelFormat.BULLET, text = "●",
                  indentLeft = 720, indentHanging = 360)
        }
        paragraphStyle(id = "BulletItem") {
            name("Bullet Item")
            basedOn("Normal")
            run { italics = true }
        }
        paragraph {
            styleReference = "BulletItem"
            numbering(reference = "my-bullets", level = 0)
            text("first")
        }
        paragraph {
            styleReference = "BulletItem"
            numbering(reference = "my-bullets", level = 0)
            text("second")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/styles.xml",
        "word/numbering.xml",
    )
}

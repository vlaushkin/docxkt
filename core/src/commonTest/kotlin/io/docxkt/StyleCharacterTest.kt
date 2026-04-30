package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class StyleCharacterTest : DocxFixtureTest("style-character") {

    override fun build(): Document = document {
        characterStyle(id = "Emphasis") {
            name("Emphasis")
            run { italics = true }
        }
        paragraph {
            text("See ")
            text("this") { styleReference = "Emphasis" }
            text(" for details.")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/styles.xml",
    )
}

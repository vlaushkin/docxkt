package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphExtrasComboTest : DocxFixtureTest("paragraph-extras-combo") {

    override fun build(): Document = document {
        paragraph {
            bidirectional = true
            contextualSpacing = true
            outlineLevel = 2
            suppressLineNumbers = true
            text("combo")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

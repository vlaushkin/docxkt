package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphIndentTest : DocxFixtureTest("paragraph-indent") {

    override fun build(): Document = document {
        paragraph {
            indent(left = 720, right = 360, firstLine = 240)
            text("Indented")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

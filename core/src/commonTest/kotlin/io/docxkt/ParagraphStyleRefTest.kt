package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphStyleRefTest : DocxFixtureTest("paragraph-style-ref") {

    override fun build(): Document = document {
        paragraph {
            styleReference = "MyStyle"
            text("styled")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

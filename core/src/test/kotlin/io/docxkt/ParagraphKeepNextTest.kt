package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphKeepNextTest : DocxFixtureTest("paragraph-keep-next") {

    override fun build(): Document = document {
        paragraph {
            keepNext = true
            text("StaysWithNext")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphWidowControlTest : DocxFixtureTest("paragraph-widow-control") {

    override fun build(): Document = document {
        paragraph {
            widowControl = false
            text("Orphaned")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

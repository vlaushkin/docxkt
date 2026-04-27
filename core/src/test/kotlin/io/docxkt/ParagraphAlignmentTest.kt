package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphAlignmentTest : DocxFixtureTest("paragraph-alignment") {

    override fun build(): Document = document {
        paragraph {
            alignment = AlignmentType.CENTER
            text("Centered")
        }
        paragraph {
            alignment = AlignmentType.RIGHT
            text("Rightward")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

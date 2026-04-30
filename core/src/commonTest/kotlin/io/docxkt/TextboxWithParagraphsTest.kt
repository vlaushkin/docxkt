package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class TextboxWithParagraphsTest : DocxFixtureTest("textbox-with-paragraphs") {

    override fun build(): Document = document {
        paragraph {
            textbox(widthEmus = 2_381_250, heightEmus = 1_428_750) {
                paragraph { text("Title") { bold = true } }
                paragraph { text("First body line.") }
                paragraph { text("Second body line.") }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

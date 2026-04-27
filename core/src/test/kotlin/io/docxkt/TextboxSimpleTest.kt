package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class TextboxSimpleTest : DocxFixtureTest("textbox-simple") {

    override fun build(): Document = document {
        paragraph {
            textbox(widthEmus = 1_905_000, heightEmus = 952_500) {
                paragraph { text("Inside textbox") }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

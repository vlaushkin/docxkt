package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FieldPageTest : DocxFixtureTest("field-page") {

    override fun build(): Document = document {
        paragraph {
            text("Page ")
            fieldComplex("PAGE")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FieldAuthorTest : DocxFixtureTest("field-author") {

    override fun build(): Document = document {
        paragraph {
            text("By ")
            fieldSimple("AUTHOR", cached = "Vasily")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

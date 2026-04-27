package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FieldDateTest : DocxFixtureTest("field-date") {

    override fun build(): Document = document {
        paragraph {
            fieldSimple("DATE \\@ \"MMM d, yyyy\"")
            text(" — today")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class FieldNumpagesTest : DocxFixtureTest("field-numpages") {

    override fun build(): Document = document {
        paragraph {
            text("Total: ")
            fieldComplex("NUMPAGES")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

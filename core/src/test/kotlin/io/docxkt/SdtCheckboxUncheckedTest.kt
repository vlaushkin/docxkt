package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SdtCheckboxUncheckedTest : DocxFixtureTest("sdt-checkbox-unchecked") {

    override fun build(): Document = document {
        paragraph {
            checkBox(checked = false)
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

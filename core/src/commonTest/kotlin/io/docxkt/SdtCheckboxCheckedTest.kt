package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SdtCheckboxCheckedTest : DocxFixtureTest("sdt-checkbox-checked") {

    override fun build(): Document = document {
        paragraph {
            checkBox(checked = true, alias = "Accept")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

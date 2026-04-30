package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.formcontrol.CheckBoxState
import io.docxkt.testing.DocxFixtureTest

internal class SdtCheckboxCustomSymbolsTest : DocxFixtureTest("sdt-checkbox-custom-symbols") {

    override fun build(): Document = document {
        paragraph {
            checkBox(
                checked = true,
                alias = "Confirm",
                checkedState = CheckBoxState(symbolHex = "F0FE", font = "Wingdings"),
                uncheckedState = CheckBoxState(symbolHex = "F0A8", font = "Wingdings"),
            )
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.formcontrol.CheckBoxState
import io.docxkt.testing.DocxFixtureTest

internal class Demo90CheckBoxesTest : DocxFixtureTest("demo-90-check-boxes") {

    override fun build(): Document = document {
        paragraph {
            text("Hello World")
            text("") { lineBreak() }
            checkBox()
            text("") { lineBreak() }
            checkBox(checked = true)
            text("") { lineBreak() }
            checkBox(
                checked = true,
                checkedState = CheckBoxState(symbolHex = "2611"),
            )
            text("") { lineBreak() }
            checkBox(
                checked = true,
                checkedState = CheckBoxState(symbolHex = "2611", font = "MS Gothic"),
            )
            text("") { lineBreak() }
            checkBox(
                checked = true,
                checkedState = CheckBoxState(symbolHex = "2611", font = "MS Gothic"),
                uncheckedState = CheckBoxState(symbolHex = "2610", font = "MS Gothic"),
            )
            text("") { lineBreak() }
            checkBox(
                checked = true,
                checkedState = CheckBoxState(symbolHex = "2611", font = "MS Gothic"),
                uncheckedState = CheckBoxState(symbolHex = "2610", font = "MS Gothic"),
            )
            text("Are you ok?") { leadingLineBreak() }
            checkBox(checked = true, alias = "Are you ok?")
        }
    }
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunComboTest : DocxFixtureTest("run-combo") {

    override fun build(): Document = document {
        paragraph {
            text("Strikethrough") {
                strike = true
                smallCaps = true
                superScript = true
            }
            text(" plain ")
            text("NoCaps") {
                allCaps = false
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunFontSimpleTest : DocxFixtureTest("run-font-simple") {

    override fun build(): Document = document {
        paragraph {
            text("ArialEverywhere") {
                font("Arial")
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

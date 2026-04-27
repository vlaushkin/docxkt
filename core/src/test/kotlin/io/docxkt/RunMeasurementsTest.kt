package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunMeasurementsTest : DocxFixtureTest("run-measurements") {

    override fun build(): Document = document {
        paragraph {
            text("measured") {
                characterSpacing = 20
                scale = 150
                kern = 28
                position = "6pt"
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

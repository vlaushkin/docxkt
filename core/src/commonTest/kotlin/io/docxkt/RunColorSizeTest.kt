package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunColorSizeTest : DocxFixtureTest("run-color-size") {

    override fun build(): Document = document {
        paragraph {
            text("Colored12pt") {
                color = "3366CC"
                size = 24
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

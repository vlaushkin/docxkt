package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunStyleRefTest : DocxFixtureTest("run-style-ref") {

    override fun build(): Document = document {
        paragraph {
            text("styled") {
                styleReference = "Heading1"
                noProof = true
                snapToGrid = false
                rightToLeft = true
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

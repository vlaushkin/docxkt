package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunTabCharacterTest : DocxFixtureTest("run-tab-character") {

    override fun build(): Document = document {
        paragraph {
            text("before")
            tab()
            text("after")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

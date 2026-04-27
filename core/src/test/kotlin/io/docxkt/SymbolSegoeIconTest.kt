package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SymbolSegoeIconTest : DocxFixtureTest("symbol-segoe-icon") {

    override fun build(): Document = document {
        paragraph {
            symbol(char = "2713", font = "Segoe UI Symbol")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

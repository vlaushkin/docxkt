package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SymbolWingdingsTest : DocxFixtureTest("symbol-wingdings") {

    override fun build(): Document = document {
        paragraph {
            symbol(char = "F0FC", font = "Wingdings")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

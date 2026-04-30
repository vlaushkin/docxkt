package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class StyleBasedOnChainTest : DocxFixtureTest("style-basedon-chain") {

    override fun build(): Document = document {
        paragraphStyle(id = "Heading") {
            name("Heading")
            basedOn("Normal")
            run { bold = true }
        }
        paragraphStyle(id = "Subheading") {
            name("Subheading")
            basedOn("Heading")
            run { italics = true }
        }
        paragraph {
            styleReference = "Heading"
            text("Top level")
        }
        paragraph {
            styleReference = "Subheading"
            text("Under it")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/styles.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class StyleParagraphHeadingTest : DocxFixtureTest("style-paragraph-heading") {

    override fun build(): Document = document {
        paragraphStyle(id = "Heading1") {
            name("heading 1")
            basedOn("Normal")
            run {
                bold = true
                size = 32
            }
        }
        paragraph {
            styleReference = "Heading1"
            text("Introduction")
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/styles.xml",
    )
}

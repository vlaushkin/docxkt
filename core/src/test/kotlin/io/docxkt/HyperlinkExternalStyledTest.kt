package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.run.UnderlineType
import io.docxkt.testing.DocxFixtureTest

internal class HyperlinkExternalStyledTest : DocxFixtureTest("hyperlink-external-styled") {

    override fun build(): Document = document {
        characterStyle(id = "Hyperlink") {
            name("Hyperlink")
            run {
                color = "0563C1"
                underline(UnderlineType.SINGLE)
            }
        }
        paragraph {
            hyperlink("https://example.com") {
                text("example.com") { styleReference = "Hyperlink" }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/styles.xml",
    )
}

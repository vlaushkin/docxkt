package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class FooterPageNumberTest : DocxFixtureTest("footer-page-number") {

    override fun build(): Document = document {
        footer {
            paragraph {
                alignment = AlignmentType.CENTER
                text("Page ")
                pageNumber()
            }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/footer1.xml",
    )
}

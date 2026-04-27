package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class FooterPageOfTotalTest : DocxFixtureTest("footer-page-of-total") {

    override fun build(): Document = document {
        footer {
            paragraph {
                alignment = AlignmentType.CENTER
                text("Page ")
                pageNumber()
                text(" of ")
                totalPages()
            }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/footer1.xml",
    )
}

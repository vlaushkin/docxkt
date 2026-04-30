package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.section.PageNumberFormat
import io.docxkt.testing.DocxFixtureTest

internal class Demo39PageNumbersTest : DocxFixtureTest("demo-39-page-numbers") {

    override fun build(): Document = document {
        header {
            paragraph {
                text("Foo Bar corp. ")
                text("Page Number ") { pageNumber() }
                text(" to ") { totalPages() }
            }
        }
        footer {
            paragraph {
                alignment = AlignmentType.CENTER
                text("Foo Bar corp. ")
                text("Page Number: ") { pageNumber() }
                text(" to ") { totalPages() }
            }
        }
        pageNumbers(start = 1, format = PageNumberFormat.DECIMAL)
        for (i in 1..5) {
            paragraph {
                text("Hello World $i")
                pageBreak()
            }
        }
    }
}

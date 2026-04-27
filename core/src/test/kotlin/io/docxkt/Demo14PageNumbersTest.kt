package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.testing.DocxFixtureTest

internal class Demo14PageNumbersTest : DocxFixtureTest("demo-14-page-numbers") {

    override fun build(): Document = document {
        titlePage()
        header {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("My Title ")
                text("Page ") { pageNumber() }
            }
        }
        header(HeaderFooterReferenceType.FIRST) {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("First Page Header ")
                text("Page ") { pageNumber() }
            }
        }
        footer {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("My Title ")
                text("Footer - Page ") {
                    pageNumber()
                    appendText(" of ")
                    totalPages()
                }
            }
        }
        footer(HeaderFooterReferenceType.FIRST) {
            paragraph {
                alignment = AlignmentType.RIGHT
                text("First Page Footer ")
                text("Page ") { pageNumber() }
            }
        }
        paragraph {
            text("First Page")
            pageBreak()
        }
        paragraph { text("Second Page") }
    }
}

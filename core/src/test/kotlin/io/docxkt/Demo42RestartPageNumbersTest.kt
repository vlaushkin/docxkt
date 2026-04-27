package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.model.section.PageNumberSeparator
import io.docxkt.testing.DocxFixtureTest

internal class Demo42RestartPageNumbersTest : DocxFixtureTest("demo-42-restart-page-numbers") {

    override fun build(): Document = document {
        // Section 1: default + first header.
        paragraph {
            text("First Page")
            pageBreak()
        }
        paragraph { text("Second Page") }
        sectionBreak {
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
        }

        // Section 2 (trailing): same H/F + pageNumbers start=1 sep=emDash.
        paragraph {
            text("Third Page")
            pageBreak()
        }
        paragraph { text("Fourth Page") }
        pageNumbers(start = 1, separator = PageNumberSeparator.EM_DASH)
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
                text("First Page Header of Second section")
                text("Page ") { pageNumber() }
            }
        }
    }
}

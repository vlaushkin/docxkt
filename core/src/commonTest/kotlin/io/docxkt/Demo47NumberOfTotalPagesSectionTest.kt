package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.section.PageNumberFormat
import io.docxkt.testing.DocxFixtureTest

internal class Demo47NumberOfTotalPagesSectionTest : DocxFixtureTest("demo-47-number-of-total-pages-section") {

    override fun build(): Document = document {
        // Section 1
        paragraph {
            text("Section 1")
            pageBreak()
            text("Section 1")
            pageBreak()
        }
        sectionBreak {
            pageNumbers(start = 1, format = PageNumberFormat.DECIMAL)
            header {
                paragraph {
                    alignment = AlignmentType.CENTER
                    text("Header on another page")
                    text("Page number: ") { pageNumber() }
                    text(" to ") { totalPagesInSection() }
                }
            }
            footer { paragraph { text("Foo Bar corp. ") } }
        }

        // Section 2 (trailing)
        paragraph {
            text("Section 2")
            pageBreak()
            text("Section 2")
            pageBreak()
        }
        pageNumbers(start = 1, format = PageNumberFormat.DECIMAL)
        header {
            paragraph {
                alignment = AlignmentType.CENTER
                text("Header on another page")
                text("Page number: ") { pageNumber() }
                text(" to ") { totalPagesInSection() }
            }
        }
        footer { paragraph { text("Foo Bar corp. ") } }
    }
}

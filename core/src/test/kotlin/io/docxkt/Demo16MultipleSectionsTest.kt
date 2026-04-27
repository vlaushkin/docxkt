package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.PageNumberFormat
import io.docxkt.model.section.PageOrientation
import io.docxkt.model.section.PageSize
import io.docxkt.testing.DocxFixtureTest

internal class Demo16MultipleSectionsTest : DocxFixtureTest("demo-16-multiple-sections") {

    override fun build(): Document = document {
        // Section 1: no headers/footers, just one paragraph.
        paragraph { text("Hello World") }
        sectionBreak {}

        // Section 2: default header + footer, pageNumbers start=1 decimal.
        paragraph { text("hello") }
        sectionBreak {
            pageNumbers(start = 1, format = PageNumberFormat.DECIMAL)
            header { paragraph { text("First Default Header on another page") } }
            footer { paragraph { text("Footer on another page") } }
        }

        // Section 3: landscape, default H/F, pageNumbers start=1 decimal.
        paragraph { text("hello in landscape") }
        sectionBreak {
            a4Landscape()
            pageNumbers(start = 1, format = PageNumberFormat.DECIMAL)
            header { paragraph { text("Second Default Header on another page") } }
            footer { paragraph { text("Footer on another page") } }
        }

        // Section 4: portrait, header only.
        paragraph { text("Page number in the header must be 2, because it continues from the previous section.") }
        sectionBreak {
            a4Portrait()
            header {
                paragraph {
                    text("Page number: ") { pageNumber() }
                }
            }
        }

        // Section 5: portrait, header only, pageNumbers upperRoman.
        paragraph { text("Page number in the header must be III, because it continues from the previous section, but is defined as upper roman.") }
        sectionBreak {
            a4Portrait()
            pageNumbers(format = PageNumberFormat.UPPER_ROMAN)
            header {
                paragraph {
                    text("Page number: ") { pageNumber() }
                }
            }
        }

        // Section 6 (trailing): portrait, pageNumbers start=25 decimal,
        // header only — uses document-level header/pageNumbers.
        paragraph { text("Page number in the header must be 25, because it is defined to start at 25 and to be decimal in this section.") }
        pageNumbers(start = 25, format = PageNumberFormat.DECIMAL)
        header {
            paragraph {
                text("Page number: ") { pageNumber() }
            }
        }
    }
}

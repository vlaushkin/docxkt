package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.model.section.SectionType
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureXml

internal class Demo81ContinuousHeaderTest : DocxFixtureTest("demo-81-continuous-header") {

    private fun loadParagraph(index: Int): String =
        loadFixtureXml("demo-81-continuous-header", "p$index.txt")

    override fun build(): Document = document {
        // Section 1: titlePage, default + first H/F.
        for (i in 0..3) paragraph { text(loadParagraph(i)) }
        // p[4] has spacing(after=500).
        paragraph {
            spacing(after = 500)
            text(loadParagraph(4))
        }
        sectionBreak {
            titlePage()
            header { paragraph { text("HEADER PAGE TWO AND FOLLOWING PAGES") } }
            header(HeaderFooterReferenceType.FIRST) {
                paragraph { text("HEADER PAGE ONE") }
            }
            footer { paragraph { text("FOOTER PAGE TWO AND FOLLOWING PAGES") } }
            footer(HeaderFooterReferenceType.FIRST) {
                paragraph { text("FOOTER PAGE ONE") }
            }
        }

        // Section 2 (trailing): type=CONTINUOUS, no own H/F.
        for (i in 6..10) paragraph { text(loadParagraph(i)) }
        sectionType(SectionType.CONTINUOUS)
    }
}

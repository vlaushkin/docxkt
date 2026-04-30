package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest
import io.docxkt.testing.loadFixtureXml

internal class Demo82NewHeadersNewSectionTest : DocxFixtureTest("demo-82-new-headers-new-section") {

    private fun loadParagraph(index: Int): String =
        // demo-81's paragraph files contain the same text.
        loadFixtureXml("demo-81-continuous-header", "p$index.txt")

    override fun build(): Document = document {
        // Section 1: default H + F.
        for (i in 0..3) paragraph { text(loadParagraph(i)) }
        paragraph {
            spacing(after = 500)
            text(loadParagraph(4))
        }
        sectionBreak {
            header { paragraph { text("HEADER PAGE ONE") } }
            footer { paragraph { text("FOOTER PAGE ONE") } }
        }

        // Section 2 (trailing): default H + F.
        for (i in 6..10) paragraph { text(loadParagraph(i)) }
        header { paragraph { text("HEADER PAGE TWO AND FOLLOWING PAGES") } }
        footer { paragraph { text("FOOTER PAGE TWO AND FOLLOWING PAGES") } }
    }
}

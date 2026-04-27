package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.testing.DocxFixtureTest

internal class Demo63OddEvenHeaderFooterTest : DocxFixtureTest("demo-63-odd-even-header-footer") {

    override fun build(): Document = document {
        header {
            paragraph { text("Odd Header text") }
            paragraph { text("Odd - Some more header text") }
        }
        header(HeaderFooterReferenceType.EVEN) {
            paragraph { text("Even header text") }
            paragraph { text("Even - Some more header text") }
        }
        footer { paragraph { text("Odd Footer text") } }
        footer(HeaderFooterReferenceType.EVEN) { paragraph { text("Even Cool Footer text") } }
        for (i in 1..5) {
            paragraph {
                text("Hello World $i")
                pageBreak()
            }
        }
    }
}

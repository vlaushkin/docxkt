package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo15PageBreakBeforeTest : DocxFixtureTest("demo-15-page-break-before") {

    override fun build(): Document = document {
        paragraph { text("Hello World") }
        paragraph {
            pageBreakBefore = true
            text("Hello World on another page")
        }
    }
}

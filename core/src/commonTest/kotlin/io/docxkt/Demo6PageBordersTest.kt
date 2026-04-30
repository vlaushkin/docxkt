package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo6PageBordersTest : DocxFixtureTest("demo-6-page-borders") {

    override fun build(): Document = document {
        margins(top = 0, right = 0, bottom = 0, left = 0)
        paragraph {
            text("Hello World")
            text("Foo bar") { bold = true }
            text("Github is the best") {
                bold = true
                tab()
            }
        }
        paragraph {
            styleReference = "Heading1"
            text("Hello World")
        }
        paragraph { text("Foo bar") }
        paragraph { text("Github is the best") }
    }
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo1BasicTest : DocxFixtureTest("demo-1-basic") {

    override fun build(): Document = document {
        paragraph {
            text("Hello World")
            text("Foo Bar") {
                bold = true
                size = 40
            }
            text("Github is the best") {
                bold = true
                tab()
            }
        }
    }
}

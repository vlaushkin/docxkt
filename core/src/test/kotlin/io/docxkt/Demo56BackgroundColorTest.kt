package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo56BackgroundColorTest : DocxFixtureTest("demo-56-background-color") {

    override fun build(): Document = document {
        background(color = "C45911")
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
            text("Github is the best") {
                bold = true
                tab()
            }
        }
    }
}

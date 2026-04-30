package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo74NodejsStreamTest : DocxFixtureTest("demo-74-nodejs-stream") {

    override fun build(): Document = document {
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

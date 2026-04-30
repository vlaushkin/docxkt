package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo19ExportToBase64Test : DocxFixtureTest("demo-19-export-to-base64") {

    override fun build(): Document = document {
        paragraph {
            text("Hello World")
            text("Foo") { bold = true }
            text("Bar") {
                bold = true
                tab()
            }
        }
    }
}

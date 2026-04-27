package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo7LandscapeTest : DocxFixtureTest("demo-7-landscape") {

    override fun build(): Document = document {
        a4Landscape()
        paragraph { text("Hello World") }
    }
}

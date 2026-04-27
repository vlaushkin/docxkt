package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo27DeclarativeStyles3Test : DocxFixtureTest("demo-27-declarative-styles-3") {

    override fun build(): Document = document {
        paragraph {
            styleReference = "myWonkyStyle"
            text("Hello")
        }
        paragraph {
            styleReference = "Heading2"
            text("World")
        }
    }
}

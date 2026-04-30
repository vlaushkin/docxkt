package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo66FieldsTest : DocxFixtureTest("demo-66-fields") {

    override fun build(): Document = document {
        paragraph {
            text("This document is called ")
            fieldSimple("FILENAME", cached = "My Document.docx")
            text(", was created on ")
            fieldSimple("""CREATEDATE  \@ "d MMMM yyyy"""")
            text(" by ")
            fieldSimple("AUTHOR")
        }
        paragraph {
            text("The document has ")
            fieldSimple("NUMWORDS", cached = "34")
            text(" words and if you'd print it ")
            bookmark("TimesPrinted") {
                text("42")
            }
            text(" times two-sided, you would need ")
            fieldSimple("=INT((TimesPrinted+1)/2)")
            text(" sheets of paper.")
        }
    }
}

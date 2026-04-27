package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo22RightToLeftTest : DocxFixtureTest("demo-22-right-to-left-text") {

    override fun build(): Document = document {
        paragraph {
            bidirectional = true
            text("שלום עולם") { rightToLeft = true }
        }
        paragraph {
            bidirectional = true
            text("שלום עולם") {
                bold = true
                rightToLeft = true
            }
        }
        paragraph {
            bidirectional = true
            text("שלום עולם") {
                italics = true
                rightToLeft = true
            }
        }
        table {
            visuallyRightToLeft = true
            row {
                cell { paragraph { text("שלום עולם") } }
                cell {}
            }
            row {
                cell {}
                cell { paragraph { text("שלום עולם") } }
            }
        }
    }
}

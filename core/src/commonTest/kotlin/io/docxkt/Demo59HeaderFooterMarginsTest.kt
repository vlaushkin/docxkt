package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo59HeaderFooterMarginsTest : DocxFixtureTest("demo-59-header-footer-margins") {

    override fun build(): Document = document {
        margins(header = 100, footer = 50)
        header {
            paragraph {
                indent(left = -400)
                text("Header text")
            }
            paragraph {
                indent(left = -600)
                text("Some more header text")
            }
        }
        footer {
            paragraph {
                indent(left = -400)
                text("Footer text")
            }
        }
        paragraph { text("Hello World") }
    }
}

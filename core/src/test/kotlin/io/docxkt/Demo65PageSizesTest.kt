package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.PageOrientation
import io.docxkt.testing.DocxFixtureTest

internal class Demo65PageSizesTest : DocxFixtureTest("demo-65-page-sizes") {

    override fun build(): Document = document {
        // Section 1: A5-ish landscape (148mm x 210mm)
        paragraph { text("Hello World") }
        sectionBreak {
            pageSize(widthTwips = 11905, heightTwips = 8390, orientation = PageOrientation.LANDSCAPE)
        }
        // Section 2: A3 portrait (297mm x 420mm)
        paragraph { text("Hello World") }
        pageSize(widthTwips = 16837, heightTwips = 23811, orientation = PageOrientation.PORTRAIT)
    }
}

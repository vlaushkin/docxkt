package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo67ColumnBreakTest : DocxFixtureTest("demo-67-column-break") {

    override fun build(): Document = document {
        columns(count = 2, spaceTwips = 708)
        paragraph {
            text("This text will be in the first column.")
            columnBreak()
            text("This text will be in the second column.")
        }
    }
}

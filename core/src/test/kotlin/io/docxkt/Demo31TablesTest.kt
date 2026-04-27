package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.table.TextDirection
import io.docxkt.model.table.VerticalAlignment
import io.docxkt.testing.DocxFixtureTest

internal class Demo31TablesTest : DocxFixtureTest("demo-31-tables") {

    override fun build(): Document = document {
        table {
            row {
                cell {
                    verticalAlign(VerticalAlignment.CENTER)
                    paragraph {}
                    paragraph {}
                }
                cell {
                    verticalAlign(VerticalAlignment.CENTER)
                    paragraph {}
                    paragraph {}
                }
                cell {
                    textDirection(TextDirection.BOTTOM_TO_TOP_LEFT_TO_RIGHT)
                    paragraph { text("bottom to top") }
                    paragraph {}
                }
                cell {
                    textDirection(TextDirection.TOP_TO_BOTTOM_RIGHT_TO_LEFT)
                    paragraph { text("top to bottom") }
                    paragraph {}
                }
            }
            row {
                cell {
                    paragraph {
                        styleReference = "Heading1"
                        text("Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah Blah")
                    }
                }
                cell {
                    verticalAlign(VerticalAlignment.CENTER)
                    paragraph { text("This text should be in the middle of the cell") }
                }
                cell {
                    verticalAlign(VerticalAlignment.CENTER)
                    paragraph { text("Text above should be vertical from bottom to top") }
                }
                cell {
                    verticalAlign(VerticalAlignment.CENTER)
                    paragraph { text("Text above should be vertical from top to bottom") }
                }
            }
        }
    }
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderStyle

internal class Demo20TableCellBordersTest : io.docxkt.testing.DocxFixtureTest("demo-20-table-cell-borders") {

    override fun build(): Document = document {
        table {
            row {
                cell {}
                cell {}
                cell {}
                cell {}
            }
            row {
                cell {}
                cell {
                    borders {
                        top = io.docxkt.model.border.BorderSide(
                            style = BorderStyle.DASH_DOT_STROKED, size = 3, color = "FF0000",
                        )
                        bottom = io.docxkt.model.border.BorderSide(
                            style = BorderStyle.DOUBLE, size = 3, color = "0000FF",
                        )
                        left = io.docxkt.model.border.BorderSide(
                            style = BorderStyle.DASH_DOT_STROKED, size = 3, color = "00FF00",
                        )
                        right = io.docxkt.model.border.BorderSide(
                            style = BorderStyle.DASH_DOT_STROKED, size = 3, color = "ff8000",
                        )
                    }
                    paragraph { text("Hello") }
                }
                cell {}
                cell {}
            }
            row {
                cell {}
                cell {}
                cell {}
                cell {}
            }
            row {
                cell {}
                cell {}
                cell {}
                cell {}
            }
        }
    }
}

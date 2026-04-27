package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.dsl.TableScope
import io.docxkt.model.table.TableWidth
import io.docxkt.model.table.TextDirection
import io.docxkt.model.table.VerticalAlignment
import io.docxkt.testing.DocxFixtureTest

internal class Demo79TableFromDataSourceTest : DocxFixtureTest("demo-79-table-from-data-source") {

    private data class Row(val date: String, val ticker: String, val price: String)

    private val data = listOf(
        Row("Tue Aug 28 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "18.12"),
        Row("Wed Aug 29 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.15"),
        Row("Thu Aug 30 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.46"),
        Row("Fri Aug 31 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.78"),
        Row("Tue Sep 04 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "20.59"),
        Row("Wed Sep 05 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.54"),
        Row("Thu Sep 06 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.29"),
        Row("Fri Sep 07 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "18.82"),
        Row("Mon Sep 10 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.53"),
        Row("Tue Sep 11 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.36"),
        Row("Wed Sep 12 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.55"),
        Row("Thu Sep 13 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.6"),
        Row("Fri Sep 14 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.83"),
        Row("Mon Sep 17 2007 00:00:00 GMT+0000 (Coordinated Universal Time)", "Apple", "19.77"),
    )

    override fun build(): Document = document {
        table {
            width(TableWidth.dxa(9070))
            row {
                headingCell("Date", TextDirection.LEFT_TO_RIGHT_TOP_TO_BOTTOM)
                headingCell("Ticker", TextDirection.LEFT_TO_RIGHT_TOP_TO_BOTTOM)
                headingCell("Price", TextDirection.TOP_TO_BOTTOM_RIGHT_TO_LEFT)
            }
            data.forEach { (date, ticker, price) ->
                row {
                    cell {
                        verticalAlign(VerticalAlignment.CENTER)
                        textDirection(TextDirection.LEFT_TO_RIGHT_TOP_TO_BOTTOM)
                        paragraph { text(date) }
                    }
                    cell {
                        verticalAlign(VerticalAlignment.CENTER)
                        textDirection(TextDirection.LEFT_TO_RIGHT_TOP_TO_BOTTOM)
                        paragraph { text(ticker) }
                    }
                    cell {
                        verticalAlign(VerticalAlignment.CENTER)
                        textDirection(TextDirection.TOP_TO_BOTTOM_RIGHT_TO_LEFT)
                        paragraph { text(price) }
                    }
                }
            }
        }
    }

    private fun io.docxkt.dsl.TableRowScope.headingCell(label: String, td: TextDirection) {
        cell {
            verticalAlign(VerticalAlignment.CENTER)
            textDirection(td)
            paragraph {
                styleReference = "Heading2"
                text(label) {
                    bold = true
                    size = 40
                }
            }
        }
    }
}

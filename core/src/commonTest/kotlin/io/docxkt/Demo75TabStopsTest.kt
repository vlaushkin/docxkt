package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.TabStop
import io.docxkt.model.paragraph.TabStopPosition
import io.docxkt.model.paragraph.TabStopType
import io.docxkt.testing.DocxFixtureTest

internal class Demo75TabStopsTest : DocxFixtureTest("demo-75-tab-stops") {

    override fun build(): Document {
        val columnWidth = TabStopPosition.MAX / 4.0
        val receiptStops = arrayOf(
            TabStop(type = TabStopType.RIGHT, position = columnWidth * 2),
            TabStop(type = TabStopType.RIGHT, position = columnWidth * 3),
            TabStop(type = TabStopType.RIGHT, position = TabStopPosition.MAX),
        )
        val twoStops = arrayOf(
            TabStop(type = TabStopType.RIGHT, position = TabStopPosition.MAX),
        )
        return document {
            settings { defaultTabStop = 0 }
            paragraph {
                styleReference = "Heading1"
                text("Receipt 001")
            }
            paragraph {
                tabs(*twoStops)
                text("To Bob.\tBy Alice.") { bold = true }
            }
            paragraph {
                tabs(*twoStops)
                text("Foo Inc\tBar Inc")
            }
            paragraph {}
            paragraph {
                tabs(*receiptStops)
                text("Item\tPrice\tQuantity\tSub-total") { bold = true }
            }
            paragraph {
                tabs(*receiptStops)
                text("Item 3\t10\t5\t50")
            }
            paragraph {
                tabs(*receiptStops)
                text("Item 3\t10\t5\t50")
            }
            paragraph {
                tabs(*receiptStops)
                text("Item 3\t10\t5\t50")
            }
            paragraph {
                tabs(*receiptStops)
                text("\t\t\tTotal: 200") { bold = true }
            }
        }
    }

}

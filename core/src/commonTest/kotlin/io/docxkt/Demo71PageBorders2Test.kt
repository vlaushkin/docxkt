package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.model.section.PageBorderDisplay
import io.docxkt.model.section.PageBorderOffsetFrom
import io.docxkt.model.section.PageBorderZOrder
import io.docxkt.testing.DocxFixtureTest

internal class Demo71PageBorders2Test : DocxFixtureTest("demo-71-page-borders-2") {

    private val lorem = "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, when an unknown printer took a galley of type and scrambled it to make a type specimen book. It has survived not only five centuries, but also the leap into electronic typesetting, remaining essentially unchanged. It was popularised in the 1960s with the release of Letraset sheets containing Lorem Ipsum passages, and more recently with desktop publishing software like Aldus PageMaker including versions of Lorem Ipsum."

    override fun build(): Document = document {
        pageBorders {
            display = PageBorderDisplay.ALL_PAGES
            offsetFrom = PageBorderOffsetFrom.TEXT
            zOrder = PageBorderZOrder.FRONT
            top = BorderSide(style = BorderStyle.SINGLE, size = 8, color = "000000")
            left = BorderSide(style = BorderStyle.SINGLE, size = 8, color = "000000")
            bottom = BorderSide(style = BorderStyle.SINGLE, size = 16, color = "000000")
            right = BorderSide(style = BorderStyle.SINGLE, size = 8, color = "FF00AA")
        }
        paragraph { text(lorem) }
        paragraph { text(lorem) }
    }
}

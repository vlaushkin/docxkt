package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.model.section.PageBorderDisplay
import io.docxkt.model.section.PageBorderOffsetFrom
import io.docxkt.model.section.PageBorderZOrder
import io.docxkt.testing.DocxFixtureTest

internal class PageBordersDecorativeTest : DocxFixtureTest("page-borders-decorative") {

    override fun build(): Document = document {
        pageBorders {
            display = PageBorderDisplay.ALL_PAGES
            offsetFrom = PageBorderOffsetFrom.PAGE
            zOrder = PageBorderZOrder.FRONT
            val side = BorderSide(style = BorderStyle.SINGLE, size = 24, color = "C00000", space = 24)
            top = side
            left = side
            bottom = side
            right = side
        }
        paragraph { text("page borders") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

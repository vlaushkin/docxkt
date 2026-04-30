package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.TabLeader
import io.docxkt.model.paragraph.TabStop
import io.docxkt.model.paragraph.TabStopType
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphTabsTest : DocxFixtureTest("paragraph-tabs") {

    override fun build(): Document = document {
        paragraph {
            tabs(
                TabStop(type = TabStopType.LEFT, position = 2000),
                TabStop(type = TabStopType.CENTER, position = 4000),
                TabStop(type = TabStopType.RIGHT, position = 9000, leader = TabLeader.DOT),
            )
            text("a\tb\tc")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

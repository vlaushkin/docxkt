package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo72WordWrapTest : DocxFixtureTest("demo-72-word-wrap") {

    private val longDigits =
        "456435234523456435564745673456345456435234523456435564745673456345" +
            "456435234523456435564745673456345456435234523456435564745673456345" +
            "456435234523456435564745673456345"

    private val loremShort =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"

    override fun build(): Document = document {
        paragraph {
            wordWrap = true
            text("我今天遛狗去公园")
            text(longDigits)
        }
        paragraph {
            wordWrap = true
            text(loremShort)
            text(longDigits)
        }
        paragraph {
            text("我今天遛狗去公园")
            text(longDigits)
        }
        paragraph {
            text(loremShort)
            text(longDigits)
        }
    }
}

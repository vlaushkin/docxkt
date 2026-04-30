package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.section.SectionVerticalAlign
import io.docxkt.testing.DocxFixtureTest

internal class Demo48VerticalAlignTest : DocxFixtureTest("demo-48-vertical-align") {

    override fun build(): Document = document {
        sectionVerticalAlign(SectionVerticalAlign.CENTER)
        paragraph {
            text("Hello World")
            text("Foo Bar") { bold = true }
            text("Github is the best") {
                bold = true
                tab()
            }
        }
    }
}

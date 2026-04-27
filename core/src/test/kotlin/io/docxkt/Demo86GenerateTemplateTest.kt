package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo86GenerateTemplateTest : DocxFixtureTest("demo-86-generate-template") {

    override fun build(): Document = document {
        paragraph {
            text("{{template}}")
        }
    }
}

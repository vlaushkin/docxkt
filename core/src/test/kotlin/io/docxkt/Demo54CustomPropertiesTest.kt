package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class Demo54CustomPropertiesTest : DocxFixtureTest("demo-54-custom-properties") {

    override fun build(): Document = document {
        properties {
            creator = "Creator"
            title = "Title"
            subject = "Subject"
            description = "Description"
            custom("Subtitle", "Subtitle")
            custom("Address", "Address")
        }
    }
}

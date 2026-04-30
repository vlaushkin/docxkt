package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MetadataFilledTest : DocxFixtureTest("metadata-filled") {

    override fun build(): Document = document {
        properties {
            title = "My Document"
            subject = "A sample"
            creator = "Vasily"
            keywords = "docxkt, sample"
            description = "A fully populated core-properties fixture"
            lastModifiedBy = "Vasily"
            revision = 3
            createdAt = "2026-04-24T00:00:00.000Z"
        }
        paragraph { text("metadata filled") }
    }

    override val comparedParts: List<String> = listOf(
        "docProps/core.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MetadataMinimalTest : DocxFixtureTest("metadata-minimal") {

    override fun build(): Document = document {
        properties {
            // Only overriding timestamps to match the publisher sentinel.
            createdAt = "2026-04-24T00:00:00.000Z"
        }
        paragraph { text("metadata minimal") }
    }

    override val comparedParts: List<String> = listOf(
        "docProps/core.xml",
        "docProps/app.xml",
        "word/settings.xml",
        "word/fontTable.xml",
    )
}

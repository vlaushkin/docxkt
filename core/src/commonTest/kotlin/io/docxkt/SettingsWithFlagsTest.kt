package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class SettingsWithFlagsTest : DocxFixtureTest("settings-with-flags") {

    override fun build(): Document = document {
        settings {
            evenAndOddHeaders = true
            trackRevisions = true
            updateFields = true
        }
        paragraph { text("settings with flags") }
    }

    override val comparedParts: List<String> = listOf(
        "word/settings.xml",
    )
}

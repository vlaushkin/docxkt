package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class CustomPropsOnlyTest : DocxFixtureTest("custom-props-only") {

    override fun build(): Document = document {
        properties {
            custom("Department", "Engineering")
            custom("Project", "Alpha")
        }
        paragraph { text("custom props only") }
    }

    override val comparedParts: List<String> = listOf(
        "docProps/custom.xml",
        "_rels/.rels",
    )
}

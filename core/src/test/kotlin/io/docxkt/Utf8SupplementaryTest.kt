package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

/**
 * The port's raison d'être: surrogate-pair round-trip. POI breaks
 * here; if this test fails, the bug is in `XmlEscape`, `DocxPackager`,
 * or the part's `toBytes` charset handling.
 */
internal class Utf8SupplementaryTest : DocxFixtureTest("utf8-supplementary") {

    override fun build(): Document = document {
        paragraph {
            text("Hello 😀")     // U+1F600 😀
            text("𠮷")            // U+20BB7 𠮷 (non-BMP CJK)
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

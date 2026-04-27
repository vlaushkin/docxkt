package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

/**
 * Insurance test: surrogate pairs through the header emission path.
 * Mirrors the body-path `utf8-supplementary` fixture — if
 * `XmlEscape` / `HeaderPart.toBytes` / ZIP charset handling
 * regresses specifically for headers, this fixture catches it.
 */
internal class HeaderUtf8SupplementaryTest : DocxFixtureTest("header-utf8-supplementary") {

    override fun build(): Document = document {
        header {
            paragraph {
                text("Caption 😀")   // U+1F600 😀
                text("𠮷")             // U+20BB7 𠮷 (non-BMP CJK)
            }
        }
        paragraph { text("body") }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "word/header1.xml",
    )
}

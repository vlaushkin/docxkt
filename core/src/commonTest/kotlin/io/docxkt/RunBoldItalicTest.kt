// Bold/italics + explicit bold=false exercises OnOff attribute-free and
// w:val="false" forms, plus the bCs/iCs mirrors.
package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunBoldItalicTest : DocxFixtureTest("run-bold-italic") {

    override fun build(): Document = document {
        paragraph {
            text("BoldItalic") {
                bold = true
                italics = true
            }
            text("NotBold") {
                bold = false
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

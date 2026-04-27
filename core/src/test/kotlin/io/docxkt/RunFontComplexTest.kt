package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class RunFontComplexTest : DocxFixtureTest("run-font-complex") {

    override fun build(): Document = document {
        paragraph {
            text("PerScript") {
                font(
                    ascii = "Calibri",
                    hAnsi = "Cambria",
                    cs = "Arial",
                    eastAsia = "MS Mincho",
                )
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

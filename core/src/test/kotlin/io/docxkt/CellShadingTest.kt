package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class CellShadingTest : DocxFixtureTest("cell-shading") {

    override fun build(): Document = document {
        table {
            row {
                cell {
                    shading(pattern = ShadingPattern.CLEAR, color = "auto", fill = "EEEEEE")
                    paragraph { text("S") }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

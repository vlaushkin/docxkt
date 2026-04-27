package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class RunBorderShadingTest : DocxFixtureTest("run-border-shading") {

    override fun build(): Document = document {
        paragraph {
            text("bordered-shaded") {
                border = BorderSide(style = BorderStyle.SINGLE, size = 4, color = "FF0000")
                shading(pattern = ShadingPattern.CLEAR, color = "auto", fill = "EEEEEE")
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

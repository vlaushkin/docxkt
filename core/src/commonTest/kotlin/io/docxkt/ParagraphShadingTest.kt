package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class ParagraphShadingTest : DocxFixtureTest("paragraph-shading") {

    override fun build(): Document = document {
        paragraph {
            shading(pattern = ShadingPattern.CLEAR, color = "auto", fill = "EEEEEE")
            text("shaded")
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

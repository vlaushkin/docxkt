package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.testing.DocxFixtureTest

internal class Demo95ParagraphStyleWithShadingAndBordersTest :
    DocxFixtureTest("demo-95-paragraph-style-with-shading-and-borders") {

    override fun build(): Document = document {
        paragraphStyle("withSingleBlackBordersAndYellowShading") {
            name("Paragraph Style with Black Borders and Yellow Shading")
            basedOn("Normal")
            paragraph {
                val side = BorderSide(
                    style = BorderStyle.SINGLE,
                    color = "000000",
                    size = 4,
                )
                borders {
                    top = side
                    bottom = side
                    left = side
                    right = side
                }
                shading(pattern = ShadingPattern.SOLID, color = "fff000")
            }
        }
        paragraph {
            styleReference = "withSingleBlackBordersAndYellowShading"
            text(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
                    "sed do eiusmod tempor incididunt ut labore et dolore magna " +
                    "aliqua. Ut enim ad minim veniam, quis nostrud exercitation " +
                    "ullamco laboris nisi ut aliquip ex ea commodo consequat."
            )
        }
    }
}

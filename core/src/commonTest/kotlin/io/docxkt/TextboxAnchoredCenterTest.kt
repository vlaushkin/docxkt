package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.textbox.TextboxBodyMargins
import io.docxkt.model.textbox.VerticalAnchor
import io.docxkt.testing.DocxFixtureTest

internal class TextboxAnchoredCenterTest : DocxFixtureTest("textbox-anchored-center") {

    override fun build(): Document = document {
        paragraph {
            textbox(widthEmus = 1_905_000, heightEmus = 952_500) {
                bodyMargins = TextboxBodyMargins(
                    leftEmus = 91_440,
                    rightEmus = 91_440,
                    topEmus = 91_440,
                    bottomEmus = 91_440,
                )
                verticalAnchor = VerticalAnchor.CENTER
                paragraph { text("Centered") }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

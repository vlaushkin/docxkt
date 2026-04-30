package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathRadicalTest : DocxFixtureTest("math-radical") {

    override fun build(): Document = document {
        paragraph {
            math {
                radical { text("x + 1") }
            }
        }
        paragraph {
            math {
                radical(degree = "3") { text("y") }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

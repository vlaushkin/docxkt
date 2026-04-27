package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathFractionSimpleTest : DocxFixtureTest("math-fraction-simple") {

    override fun build(): Document = document {
        paragraph {
            math {
                fraction {
                    numerator { text("a") }
                    denominator { text("b") }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

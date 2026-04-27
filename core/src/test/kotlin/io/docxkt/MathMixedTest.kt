package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathMixedTest : DocxFixtureTest("math-mixed") {

    override fun build(): Document = document {
        paragraph {
            math {
                fraction {
                    numerator {
                        brackets {
                            text("a + ")
                            radical { text("b") }
                        }
                    }
                    denominator {
                        text("c")
                    }
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

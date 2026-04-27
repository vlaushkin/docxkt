package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathNarySumTest : DocxFixtureTest("math-nary-sum") {

    override fun build(): Document = document {
        paragraph {
            math {
                sum(subScript = "i=1", superScript = "n") {
                    text("x")
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathBracketsSquareTest : DocxFixtureTest("math-brackets-square") {

    override fun build(): Document = document {
        paragraph {
            math {
                brackets(begin = "[", end = "]") {
                    text("a, b")
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

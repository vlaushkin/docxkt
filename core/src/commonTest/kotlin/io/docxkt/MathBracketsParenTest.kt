package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathBracketsParenTest : DocxFixtureTest("math-brackets-paren") {

    override fun build(): Document = document {
        paragraph {
            math {
                brackets {
                    text("x + y")
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

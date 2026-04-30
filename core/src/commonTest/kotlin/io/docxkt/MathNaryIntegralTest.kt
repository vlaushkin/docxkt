package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathNaryIntegralTest : DocxFixtureTest("math-nary-integral") {

    override fun build(): Document = document {
        paragraph {
            math {
                integral(subScript = "0", superScript = "1") {
                    text("f(x) dx")
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

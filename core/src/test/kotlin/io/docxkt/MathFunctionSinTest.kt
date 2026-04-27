package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathFunctionSinTest : DocxFixtureTest("math-function-sin") {

    override fun build(): Document = document {
        paragraph {
            math {
                function(name = "sin") {
                    text("x")
                }
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathSubscriptTest : DocxFixtureTest("math-subscript") {

    override fun build(): Document = document {
        paragraph {
            math {
                sub(
                    configure = { text("x") },
                    subScript = { text("i") },
                )
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

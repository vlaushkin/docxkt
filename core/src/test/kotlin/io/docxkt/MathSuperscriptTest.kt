package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathSuperscriptTest : DocxFixtureTest("math-superscript") {

    override fun build(): Document = document {
        paragraph {
            math {
                sup(
                    configure = { text("x") },
                    superScript = { text("2") },
                )
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.testing.DocxFixtureTest

internal class MathPreSubSuperTest : DocxFixtureTest("math-pre-sub-super") {

    override fun build(): Document = document {
        paragraph {
            math {
                preSubSuper(
                    configure = { text("T") },
                    subScript = { text("i") },
                    superScript = { text("j") },
                )
            }
        }
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

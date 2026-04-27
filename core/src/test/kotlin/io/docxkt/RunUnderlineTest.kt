package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.paragraph.run.UnderlineType
import io.docxkt.testing.DocxFixtureTest

internal class RunUnderlineTest : DocxFixtureTest("run-underline") {

    override fun build(): Document = document {
        paragraph {
            text("UnderlinedRed") {
                underline(type = UnderlineType.DOUBLE, color = "FF0000")
            }
        }
    }

    override val comparedParts: List<String> = listOf("word/document.xml")
}

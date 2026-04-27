package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo8HeaderFooterTest : DocxFixtureTest("demo-8-header-footer") {

    override fun build(): Document = document {
        listTemplate("footer-numbering") {
            // upstream: convertInchesToTwip(0.5) = 720; convertInchesToTwip(0.18) = 259.
            level(
                level = 0,
                format = LevelFormat.DECIMAL,
                text = "%1.",
                justification = AlignmentType.START,
                indentLeft = 720,
                indentHanging = 259,
            )
        }
        header { paragraph { text("Header text") } }
        footer {
            paragraph { text("This footer contains a numbered list:") }
            paragraph {
                numbering("footer-numbering", 0)
                text("First item in the list")
            }
            paragraph {
                numbering("footer-numbering", 0)
                text("Second item in the list")
            }
            paragraph {
                numbering("footer-numbering", 0)
                text("Third item in the list")
            }
        }
        paragraph { text("Hello World") }
    }
}

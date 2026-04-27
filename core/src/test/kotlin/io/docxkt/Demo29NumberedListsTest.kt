package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo29NumberedListsTest : DocxFixtureTest("demo-29-numbered-lists") {

    override fun build(): Document = document {
        listTemplate("my-crazy-reference") {
            level(
                level = 0,
                format = LevelFormat.UPPER_ROMAN,
                text = "%1",
                justification = AlignmentType.START,
                indentLeft = 720,
                indentHanging = 259,
            )
        }
        listTemplate("my-number-numbering-reference") {
            level(
                level = 0,
                format = LevelFormat.DECIMAL,
                text = "%1",
                justification = AlignmentType.START,
                indentLeft = 720,
                indentHanging = 259,
            )
        }
        listTemplate("padded-numbering-reference") {
            level(
                level = 0,
                format = LevelFormat.DECIMAL_ZERO,
                text = "[%1]",
                justification = AlignmentType.START,
                indentLeft = 720,
                indentHanging = 259,
            )
        }
        // 4 paragraphs in my-crazy-reference (instance 0), with contextualSpacing variants.
        paragraph {
            numbering("my-crazy-reference", level = 0)
            contextualSpacing = true
            spacing(before = 200)
            text("line with contextual spacing")
        }
        paragraph {
            numbering("my-crazy-reference", level = 0)
            contextualSpacing = true
            spacing(before = 200)
            text("line with contextual spacing")
        }
        paragraph {
            numbering("my-crazy-reference", level = 0)
            contextualSpacing = false
            spacing(before = 200)
            text("line without contextual spacing")
        }
        paragraph {
            numbering("my-crazy-reference", level = 0)
            contextualSpacing = false
            spacing(before = 200)
            text("line without contextual spacing")
        }
        // 3 paragraphs in my-number-numbering-reference (instance 0).
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 1 - Add sugar")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 2 - Add wheat")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 3 - Put in oven")
        }
        // Heading2 paragraph.
        paragraph {
            styleReference = "Heading2"
            text("Next")
        }
        // 2 paragraphs in padded-numbering-reference instance 2.
        paragraph {
            numbering("padded-numbering-reference", level = 0, instance = 2)
            text("test")
        }
        paragraph {
            numbering("padded-numbering-reference", level = 0, instance = 2)
            text("test")
        }
        paragraph {
            styleReference = "Heading2"
            text("Next")
        }
        // 3 paragraphs in padded-numbering-reference instance 3.
        paragraph {
            numbering("padded-numbering-reference", level = 0, instance = 3)
            text("test")
        }
        paragraph {
            numbering("padded-numbering-reference", level = 0, instance = 3)
            text("test")
        }
        paragraph {
            numbering("padded-numbering-reference", level = 0, instance = 3)
            text("test")
        }
        paragraph {
            styleReference = "Heading2"
            text("Next")
        }
        // 14 paragraphs in padded-numbering-reference instance 0 (default).
        repeat(14) {
            paragraph {
                numbering("padded-numbering-reference", level = 0)
                text("test")
            }
        }
    }
}

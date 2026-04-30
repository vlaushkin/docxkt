package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo57AddParentNumberedListsTest :
    DocxFixtureTest("demo-57-add-parent-numbered-lists") {

    override fun build(): Document = document {
        listTemplate("my-number-numbering-reference") {
            level(
                level = 0,
                format = LevelFormat.DECIMAL,
                text = "%1",
                justification = AlignmentType.START,
                indentLeft = 720, // convertInchesToTwip(0.5)
                indentHanging = 260,
            )
            // Level 1: indent left = 1.25 * 720 = 900, hanging = 1.25 * 260 = 325.
            level(
                level = 1,
                format = LevelFormat.DECIMAL,
                text = "%1.%2",
                justification = AlignmentType.START,
                indentLeft = 900,
                indentHanging = 325,
            ) {
                bold = true
                size = 18
                font(name = "Times New Roman")
            }
        }
        paragraph {
            styleReference = "Heading1"
            text("How to make cake")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 1 - Add sugar")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 2 - Add wheat")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 1)
            text("Step 2a - Stir the wheat in a circle")
        }
        paragraph {
            numbering("my-number-numbering-reference", level = 0)
            text("Step 3 - Put in oven")
        }
        paragraph {
            styleReference = "Heading1"
            text("How to make cake")
        }
    }
}

package io.docxkt

import io.docxkt.api.Document
import io.docxkt.api.document
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.testing.DocxFixtureTest

internal class Demo3NumberingAndBulletPointsTest :
    DocxFixtureTest("demo-3-numbering-and-bullet-points") {

    override fun build(): Document = document {
        listTemplate("my-crazy-numbering") {
            level(
                level = 0, format = LevelFormat.UPPER_ROMAN, text = "%1",
                justification = AlignmentType.START,
                indentLeft = 720, indentHanging = 259,
            )
            level(
                level = 1, format = LevelFormat.DECIMAL, text = "%2.",
                justification = AlignmentType.START,
                indentLeft = 1440, indentHanging = 980,
            )
            level(
                level = 2, format = LevelFormat.LOWER_LETTER, text = "%3)",
                justification = AlignmentType.START,
                indentLeft = 2160, indentHanging = 1700,
            )
            level(
                level = 3, format = LevelFormat.UPPER_LETTER, text = "%4)",
                justification = AlignmentType.START,
                indentLeft = 2880, indentHanging = 2420,
            )
        }
        listTemplate("my-unique-bullet-points") {
            level(
                level = 0, format = LevelFormat.BULLET, text = "ὠ",
                justification = AlignmentType.LEFT,
                indentLeft = 720, indentHanging = 360,
            )
            level(
                level = 1, format = LevelFormat.BULLET, text = "¥",
                justification = AlignmentType.LEFT,
                indentLeft = 1440, indentHanging = 360,
            )
            level(
                level = 2, format = LevelFormat.BULLET, text = "✿",
                justification = AlignmentType.LEFT,
                indentLeft = 2160, indentHanging = 360,
            )
            level(
                level = 3, format = LevelFormat.BULLET, text = "♺",
                justification = AlignmentType.LEFT,
                indentLeft = 2880, indentHanging = 360,
            )
            level(
                level = 4, format = LevelFormat.BULLET, text = "☃",
                justification = AlignmentType.LEFT,
                indentLeft = 3600, indentHanging = 360,
            )
        }
        // Header / Footer use the same numbering reference (instance 0).
        header {
            paragraph {
                numbering("my-crazy-numbering", level = 0)
                text("Hey you")
            }
            paragraph {
                numbering("my-crazy-numbering", level = 1)
                text("What's up fam")
            }
        }
        footer {
            paragraph {
                numbering("my-crazy-numbering", level = 0)
                text("Hey you")
            }
            paragraph {
                numbering("my-crazy-numbering", level = 1)
                text("What's up fam")
            }
        }
        paragraph {
            numbering("my-crazy-numbering", level = 0)
            text("Hey you")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 1)
            text("What's up fam")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 1)
            text("Hello World 2")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 2)
            text("Yeah boi")
        }
        paragraph {
            bullet(level = 0)
            text("Hey you")
        }
        paragraph {
            bullet(level = 1)
            text("What's up fam")
        }
        paragraph {
            bullet(level = 2)
            text("Hello World 2")
        }
        paragraph {
            bullet(level = 3)
            text("Yeah boi")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 3)
            text("101 MSXFM")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 1)
            text("back to level 1")
        }
        paragraph {
            numbering("my-crazy-numbering", level = 0)
            text("back to level 0")
        }
        paragraph {
            styleReference = "Heading1"
            text("Custom Bullet points")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 0)
            text("What's up fam")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 0)
            text("Hey you")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 1)
            text("What's up fam")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 2)
            text("Hello World 2")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 3)
            text("Yeah boi")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 4)
            text("my Awesome numbering")
        }
        paragraph {
            numbering("my-unique-bullet-points", level = 1)
            text("Back to level 1")
        }
    }
}

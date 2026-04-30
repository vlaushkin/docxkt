// Port of: src/file/numbering/numbering.ts (default-bullet-numbering
// hardcoded in Numbering constructor, L124-L244).
package io.docxkt.model.numbering

import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.Indentation

/**
 * Upstream's `Numbering` class always pushes a 9-level bullet
 * abstractNum (id=1) and a concrete `<w:num w:numId="1">`
 * referencing it — even when the document carries no list
 * templates. We mirror byte-for-byte: every document emits
 * `numbering.xml` with this phantom + any user templates.
 *
 * Indent pattern: left = `(level + 1) * 720`, hanging = `360` for
 * every level. Bullet glyph cycles through ● ○ ■ for levels 0..5,
 * then ● for 6/7/8.
 */
internal object DefaultBulletNumbering {
    val ABSTRACT_NUM_ID: Int = 1
    val NUM_ID: Int = 1

    private val GLYPHS = listOf("●", "○", "■", "●", "○", "■", "●", "●", "●")

    val LEVELS: List<NumberingLevel> = (0..8).map { level ->
        NumberingLevel(
            level = level,
            format = LevelFormat.BULLET,
            text = GLYPHS[level],
            start = 1,
            justification = AlignmentType.LEFT,
            indentation = Indentation(
                left = (level + 1) * 720,
                hanging = 360,
            ),
        )
    }

    val ABSTRACT: AbstractNumbering = AbstractNumbering(
        abstractNumId = ABSTRACT_NUM_ID,
        levels = LEVELS,
    )

    val CONCRETE: ConcreteNumbering = ConcreteNumbering(
        numId = NUM_ID,
        abstractNumId = ABSTRACT_NUM_ID,
        startOverride = 1,
    )
}

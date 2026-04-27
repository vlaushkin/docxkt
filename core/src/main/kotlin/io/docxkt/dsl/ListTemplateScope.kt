// No upstream analogue — DSL scope receiver for list-template
// registration.
package io.docxkt.dsl

import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.Indentation
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.numbering.NumberingLevel

/**
 * Builder for a single list template. Called via
 * `DocumentScope.listTemplate(reference) { level(...) ... }`.
 *
 * Each `level(...)` call appends a `<w:lvl>` entry. Order matches
 * insertion order; levels should be supplied in ascending `ilvl`
 * order (0, 1, 2, …) — mismatches are a caller error the library
 * doesn't guard against.
 */
@DocxktDsl
public class ListTemplateScope internal constructor() {
    private val levels = mutableListOf<NumberingLevel>()

    /**
     * Append one `<w:lvl>` entry.
     *
     * @param level Zero-based level index (0 = outermost).
     * @param format Number format (`LevelFormat.DECIMAL`,
     *               `LevelFormat.BULLET`, …).
     * @param text The level-text template, e.g. `"%1."` for decimal,
     *             `"●"` for a bullet glyph.
     * @param start Starting number (default `1`).
     * @param justification `<w:lvlJc>` — defaults to `LEFT`.
     * @param indentLeft / indentHanging Indent in twips; both
     *             optional. When either is set, an `<w:ind>` child
     *             is emitted inside the level's `<w:pPr>`.
     */
    public fun level(
        level: Int,
        format: LevelFormat,
        text: String,
        start: Int = 1,
        justification: AlignmentType = AlignmentType.LEFT,
        indentLeft: Int? = null,
        indentHanging: Int? = null,
        configure: RunScope.() -> Unit = {},
    ) {
        val indent = if (indentLeft != null || indentHanging != null) {
            Indentation(left = indentLeft, hanging = indentHanging)
        } else {
            null
        }
        // Optional `<w:rPr>` inside `<w:lvl>` for level-specific
        // run formatting (matches upstream's `style: { run: { ... } }`
        // block). RunProperties is IgnoreIfEmptyXmlComponent — passing
        // the always-built value is safe; an unset `configure` lambda
        // yields a no-op rPr that the parent's emit skips.
        val runScope = RunScope().apply(configure)
        val runProps = runScope.buildProperties()
        levels += NumberingLevel(
            level = level,
            format = format,
            text = text,
            start = start,
            justification = justification,
            indentation = indent,
            runProperties = runProps,
        )
    }

    internal fun build(): List<NumberingLevel> = levels.toList()
}

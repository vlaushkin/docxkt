// Port of: src/file/paragraph/formatting/spacing.ts (ISpacingProperties).
package io.docxkt.model.paragraph

/**
 * `<w:spacing>` attributes.
 *
 * Values in twips for [before], [after], [line]. [lineRule] is optional
 * — OOXML defaults it to `auto` when absent, matching upstream. No
 * coupling validation (upstream does not enforce it either).
 *
 * Attribute order for emission: `after, before, line, lineRule,
 * beforeAutoSpacing, afterAutoSpacing` — matches upstream's BuilderElement.
 */
public data class Spacing(
    val after: Int? = null,
    val before: Int? = null,
    val line: Int? = null,
    val lineRule: LineRule? = null,
    val beforeAutoSpacing: Boolean? = null,
    val afterAutoSpacing: Boolean? = null,
) {
    internal fun isEmpty(): Boolean =
        after == null && before == null && line == null && lineRule == null &&
            beforeAutoSpacing == null && afterAutoSpacing == null
}

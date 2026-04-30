// Port of: src/file/paragraph/formatting/spacing.ts (LineRuleType object).
package io.docxkt.model.paragraph

/**
 * Line-height interpretation for `<w:spacing w:lineRule>`:
 *
 * - `AUTO` — line value is 240ths of a line; Word computes height.
 * - `AT_LEAST` — at least the given twips value, grow for tall content.
 * - `EXACTLY` / `EXACT` — exactly this many twips; tall content clips.
 *   Upstream keeps both spellings; we preserve both for source parity.
 */
public enum class LineRule(internal val wire: String) {
    AT_LEAST("atLeast"),
    EXACTLY("exactly"),
    EXACT("exact"),
    AUTO("auto"),
}

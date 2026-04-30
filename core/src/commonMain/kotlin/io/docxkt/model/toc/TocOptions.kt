// Port of: src/file/table-of-contents/table-of-contents-properties.ts
// (ITableOfContentsOptions subset — the four most commonly-used flags;
// others can be added one-field-per-consumer).
package io.docxkt.model.toc

/**
 * Flags controlling the TOC instruction string emitted into
 * `<w:instrText>`. Each flag maps to an upstream switch char:
 *
 * - [hyperlink] → `\h` — generate hyperlinks for each entry.
 * - [headingStyleRange] → `\o "LOW-HIGH"` — heading levels to
 *   include (e.g. `"1-3"`).
 * - [useAppliedParagraphOutlineLevel] → `\u` — include body
 *   paragraphs that carry an `<w:outlineLvl/>`.
 * - [hideTabAndPageNumbersInWebView] → `\z` — hide tab leaders
 *   and page numbers when rendered in web layout.
 *
 * Instruction concatenation order matches upstream's
 * `FieldInstruction` `if` chain: hyperlink before headingStyleRange
 * before useAppliedParagraphOutlineLevel before
 * hideTabAndPageNumbersInWebView.
 */
internal data class TocOptions(
    val hyperlink: Boolean = false,
    val headingStyleRange: String? = null,
    val useAppliedParagraphOutlineLevel: Boolean = false,
    val hideTabAndPageNumbersInWebView: Boolean = false,
) {
    /**
     * Build the raw `TOC …` instruction body. Upstream doesn't
     * escape the double quotes around values — but the `<w:instrText>`
     * body is escaped by the caller (the part emitter).
     */
    fun buildInstruction(): String {
        val sb = StringBuilder("TOC")
        if (hyperlink) sb.append(" \\h")
        if (headingStyleRange != null) {
            sb.append(" \\o \"").append(headingStyleRange).append('"')
        }
        if (useAppliedParagraphOutlineLevel) sb.append(" \\u")
        if (hideTabAndPageNumbersInWebView) sb.append(" \\z")
        return sb.toString()
    }
}

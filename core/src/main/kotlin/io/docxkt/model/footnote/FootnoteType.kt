// Port of: src/file/footnotes/footnote/footnote.ts (FootnoteType).
package io.docxkt.model.footnote

/**
 * The `w:type` attribute value on `<w:footnote>` (and on
 * `<w:endnote>` — the endnote part reuses the same type tokens).
 *
 * `USER` maps to a missing attribute (user-defined notes don't
 * carry `w:type`).
 */
internal enum class FootnoteType(val wire: String?) {
    SEPARATOR("separator"),
    CONTINUATION_SEPARATOR("continuationSeparator"),
    USER(null),
}

// Port of: src/file/paragraph/run/language.ts (ILanguageOptions).
package io.docxkt.model.paragraph.run

/**
 * `<w:lang>` attribute triple — one language per script type.
 *
 * RFC 1766 tags (`"en-US"`, `"ja-JP"`, `"ar-SA"`, ...). Each field
 * is optional; the emitter omits the attribute when the value is
 * `null`.
 *
 * Attribute order in the wire: `val, eastAsia, bidi` — matches
 * upstream's `BuilderElement` definition, not alphabetical.
 */
public data class Language(
    /** Latin and complex-script text. Wire attribute: `w:val`. */
    val value: String? = null,
    /** East Asian text. Wire attribute: `w:eastAsia`. */
    val eastAsia: String? = null,
    /** Bidirectional text (Arabic, Hebrew, ...). Wire attribute: `w:bidi`. */
    val bidirectional: String? = null,
) {
    internal fun isEmpty(): Boolean =
        value == null && eastAsia == null && bidirectional == null
}

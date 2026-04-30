// Port of: src/file/paragraph/formatting/indent.ts (IIndentAttributesProperties).
package io.docxkt.model.paragraph

/**
 * `<w:ind>` attributes — all in twips.
 *
 * Upstream also accepts universal-measure strings ("1in", "2.5cm"); we
 * take twips only and defer the string-measure overload. Attribute
 * order for emission: `start, end, left, right, hanging, firstLine`
 * — matches upstream's `BuilderElement` definition.
 */
public data class Indentation(
    val start: Int? = null,
    val end: Int? = null,
    val left: Int? = null,
    val right: Int? = null,
    val hanging: Int? = null,
    val firstLine: Int? = null,
) {
    internal fun isEmpty(): Boolean =
        start == null && end == null && left == null && right == null &&
            hanging == null && firstLine == null
}

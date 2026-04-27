// Port of: src/file/paragraph/run/emphasis-mark.ts (EmphasisMarkType).
package io.docxkt.model.paragraph.run

/**
 * Emphasis-mark value for `<w:em w:val="...">`.
 *
 * The OOXML XSD permits `none / dot / comma / circle / underDot`,
 * but upstream's `EmphasisMarkType` only exports `DOT`. We match
 * the narrower surface — add more variants when a consumer asks.
 */
public enum class EmphasisMark(internal val wire: String) {
    DOT("dot"),
}

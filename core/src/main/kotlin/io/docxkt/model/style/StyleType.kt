// Port of: src/file/styles/style/paragraph-style.ts + character-style.ts
// (type attribute on `<w:style>`). Upstream also ships `table` and
// `numbering` values — not yet supported here.
package io.docxkt.model.style

/**
 * The `w:type` attribute on `<w:style>`. Tags which kind of reference
 * can resolve to this style definition:
 *
 * - [PARAGRAPH] — resolved from `<w:pStyle w:val="…"/>` inside `<w:pPr>`.
 * - [CHARACTER] — resolved from `<w:rStyle w:val="…"/>` inside `<w:rPr>`.
 */
internal enum class StyleType(val wire: String) {
    PARAGRAPH("paragraph"),
    CHARACTER("character"),
}

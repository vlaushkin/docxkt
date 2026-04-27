// Port of: src/file/vertical-align/vertical-align.ts (VerticalAlignSection)
package io.docxkt.model.section

/**
 * Section-level `<w:vAlign w:val="…"/>` value (inside `<w:sectPr>`).
 * Adds `BOTH` on top of the table-cell vertical-alignment set.
 *
 * Sibling enums for distinct domains:
 *
 * - [io.docxkt.model.table.VerticalAlignment] — `<w:tcPr><w:vAlign>`
 *   inside table cells.
 * - [io.docxkt.model.textbox.VerticalAnchor] — `wps:bodyPr@anchor`
 *   inside textbox shapes.
 * - [io.docxkt.model.drawing.VerticalAlign] — `wp:positionV`
 *   anchor align tokens for floating drawings.
 */
public enum class SectionVerticalAlign(internal val wire: String) {
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom"),
    BOTH("both"),
}

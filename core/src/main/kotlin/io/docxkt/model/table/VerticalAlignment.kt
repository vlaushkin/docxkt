// Port of: src/file/vertical-align/vertical-align.ts (VerticalAlignTable).
package io.docxkt.model.table

/**
 * Vertical alignment **inside a table cell** (`<w:tcPr><w:vAlign>`).
 *
 * Per ECMA-376 §17.18.87, `ST_VerticalJc` inside `<w:tcPr>` is
 * restricted to `top / center / bottom`. Sibling enums for distinct
 * domains:
 *
 * - [io.docxkt.model.section.SectionVerticalAlign] — `<w:sectPr>`,
 *   adds `BOTH` for distributed-justify.
 * - [io.docxkt.model.textbox.VerticalAnchor] — `wps:bodyPr@anchor`
 *   inside textbox shapes.
 * - [io.docxkt.model.drawing.VerticalAlign] — `wp:positionV`
 *   anchor align tokens for floating drawings.
 */
public enum class VerticalAlignment(internal val wire: String) {
    TOP("top"),
    CENTER("center"),
    BOTTOM("bottom"),
}

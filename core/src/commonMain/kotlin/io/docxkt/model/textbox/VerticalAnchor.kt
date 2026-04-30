// Port of: src/file/drawing/inline/graphic/graphic-data/wps/body-properties.ts
// (VerticalAnchor enum).
package io.docxkt.model.textbox

/**
 * `wps:bodyPr@anchor` — vertical text alignment **inside a textbox
 * shape**.
 *
 * Sibling enums for distinct domains:
 *
 * - [io.docxkt.model.table.VerticalAlignment] — `<w:tcPr><w:vAlign>`
 *   inside table cells.
 * - [io.docxkt.model.section.SectionVerticalAlign] — `<w:sectPr>`,
 *   adds `BOTH` for distributed-justify.
 * - [io.docxkt.model.drawing.VerticalAlign] — `wp:positionV`
 *   anchor align tokens for floating drawings.
 */
public enum class VerticalAnchor(internal val wire: String) {
    TOP("t"),
    CENTER("ctr"),
    BOTTOM("b"),
}

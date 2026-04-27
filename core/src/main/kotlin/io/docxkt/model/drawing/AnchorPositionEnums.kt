// Port of: src/file/drawing/floating/floating-position.ts
//          (HorizontalPositionRelativeFrom, VerticalPositionRelativeFrom)
//        + src/file/shared/alignment.ts
//          (HorizontalPositionAlign, VerticalPositionAlign)
package io.docxkt.model.drawing

/**
 * `relativeFrom` attribute on `<wp:positionH>`.
 * `ST_RelFromH` enumeration.
 */
public enum class HorizontalRelativeFrom(internal val wire: String) {
    CHARACTER("character"),
    COLUMN("column"),
    INSIDE_MARGIN("insideMargin"),
    LEFT_MARGIN("leftMargin"),
    MARGIN("margin"),
    OUTSIDE_MARGIN("outsideMargin"),
    PAGE("page"),
    RIGHT_MARGIN("rightMargin"),
}

/**
 * `relativeFrom` attribute on `<wp:positionV>`.
 * `ST_RelFromV` enumeration.
 */
public enum class VerticalRelativeFrom(internal val wire: String) {
    BOTTOM_MARGIN("bottomMargin"),
    INSIDE_MARGIN("insideMargin"),
    LINE("line"),
    MARGIN("margin"),
    OUTSIDE_MARGIN("outsideMargin"),
    PAGE("page"),
    PARAGRAPH("paragraph"),
    TOP_MARGIN("topMargin"),
}

/**
 * `<wp:align>` body for **horizontal alignment of a floating
 * drawing anchor** (`ST_AlignH`).
 *
 * Distinct from [io.docxkt.model.paragraph.AlignmentType], which
 * is paragraph-level `<w:jc>` justification — different value set
 * (start/end/both/distribute), different wire location.
 */
public enum class HorizontalAlign(internal val wire: String) {
    CENTER("center"),
    INSIDE("inside"),
    LEFT("left"),
    OUTSIDE("outside"),
    RIGHT("right"),
}

/**
 * `<wp:align>` body for **vertical alignment of a floating drawing
 * anchor** (`ST_AlignV`).
 *
 * Sibling enums for distinct domains:
 *
 * - [io.docxkt.model.table.VerticalAlignment] — `<w:tcPr><w:vAlign>`
 *   inside table cells.
 * - [io.docxkt.model.section.SectionVerticalAlign] — `<w:sectPr>`,
 *   adds `BOTH` for distributed-justify.
 * - [io.docxkt.model.textbox.VerticalAnchor] — `wps:bodyPr@anchor`
 *   inside textbox shapes.
 */
public enum class VerticalAlign(internal val wire: String) {
    BOTTOM("bottom"),
    CENTER("center"),
    INSIDE("inside"),
    OUTSIDE("outside"),
    TOP("top"),
}

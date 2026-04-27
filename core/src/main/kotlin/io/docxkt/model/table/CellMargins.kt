// Port of: src/file/table/table-properties/table-cell-margin.ts
// (createTableCellMargin + createCellMargin + buildMarginChildren).
package io.docxkt.model.table

/**
 * Cell padding at either the table level (`<w:tblCellMar>`) or the
 * per-cell level (`<w:tcMar>`). Both have the same shape — four
 * optional sides, each a `<w:tblWidth>`-style width/unit pair.
 *
 * Values are in twips by default (upstream's DXA). Upstream emits a
 * side only when the caller set it; we match. If all four sides are
 * `null` the whole block is suppressed at the call site (see
 * [writeCellMargins]).
 *
 * Side order in the wire: `top, left, bottom, right`, matching
 * upstream's `buildMarginChildren` array.
 */
public data class CellMargins(
    val top: Int? = null,
    val left: Int? = null,
    val bottom: Int? = null,
    val right: Int? = null,
    val unit: WidthType = WidthType.DXA,
) {
    internal fun isEmpty(): Boolean =
        top == null && left == null && bottom == null && right == null
}

/**
 * Emit `<name>...</name>` containing the four margin sides the caller
 * set. Does nothing when [margins] is empty. The element name is
 * `w:tcMar` for per-cell margins and `w:tblCellMar` for table-level
 * defaults.
 */
internal fun writeCellMargins(out: Appendable, elementName: String, margins: CellMargins) {
    if (margins.isEmpty()) return
    out.append('<').append(elementName).append('>')
    margins.top?.let { writeWidth(out, "w:top", TableWidth(it, margins.unit)) }
    margins.left?.let { writeWidth(out, "w:left", TableWidth(it, margins.unit)) }
    margins.bottom?.let { writeWidth(out, "w:bottom", TableWidth(it, margins.unit)) }
    margins.right?.let { writeWidth(out, "w:right", TableWidth(it, margins.unit)) }
    out.append("</").append(elementName).append('>')
}

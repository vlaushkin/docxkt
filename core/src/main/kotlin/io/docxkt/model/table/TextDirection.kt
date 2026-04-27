// Port of: src/file/table/table-cell/table-cell-components.ts (TextDirection enum)
package io.docxkt.model.table

/**
 * Text-flow direction inside a table cell (`<w:textDirection w:val="…"/>`).
 *
 * - [LEFT_TO_RIGHT_TOP_TO_BOTTOM] — `lrTb`, the default LTR flow.
 * - [TOP_TO_BOTTOM_RIGHT_TO_LEFT] — `tbRl`, vertical CJK flow.
 * - [BOTTOM_TO_TOP_LEFT_TO_RIGHT] — `btLr`, rotated 90° CCW.
 */
public enum class TextDirection(internal val wire: String) {
    BOTTOM_TO_TOP_LEFT_TO_RIGHT("btLr"),
    LEFT_TO_RIGHT_TOP_TO_BOTTOM("lrTb"),
    TOP_TO_BOTTOM_RIGHT_TO_LEFT("tbRl"),
}

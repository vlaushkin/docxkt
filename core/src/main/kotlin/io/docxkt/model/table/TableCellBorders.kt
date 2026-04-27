// Port of: src/file/table/table-cell/table-cell-components.ts (TableCellBorders).
package io.docxkt.model.table

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.writeBorderSide
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `<w:tcBorders>` — cell-level borders.
 *
 * Unlike [TableBorders], which always emits all six sides, `tcBorders`
 * emits only the sides the caller explicitly set — matching upstream's
 * per-side `if (options.side) { push(...) }` pattern. An all-null
 * instance is suppressed via [IgnoreIfEmptyXmlComponent].
 *
 * Side order when emitted: top, start, left, bottom, end, right — same
 * as the XSD sequence.
 */
internal class TableCellBorders(
    val top: BorderSide? = null,
    val start: BorderSide? = null,
    val left: BorderSide? = null,
    val bottom: BorderSide? = null,
    val end: BorderSide? = null,
    val right: BorderSide? = null,
) : IgnoreIfEmptyXmlComponent("w:tcBorders") {

    override fun isEmpty(): Boolean = (
        top == null && start == null && left == null &&
            bottom == null && end == null && right == null
    )

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:tcBorders")
        top?.let { writeBorderSide(out, "w:top", it) }
        start?.let { writeBorderSide(out, "w:start", it) }
        left?.let { writeBorderSide(out, "w:left", it) }
        bottom?.let { writeBorderSide(out, "w:bottom", it) }
        end?.let { writeBorderSide(out, "w:end", it) }
        right?.let { writeBorderSide(out, "w:right", it) }
        out.closeElement("w:tcBorders")
    }
}

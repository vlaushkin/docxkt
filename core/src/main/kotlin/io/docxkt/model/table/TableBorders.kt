// Port of: src/file/table/table-properties/table-borders.ts (TableBorders).
package io.docxkt.model.table

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.writeBorderSide
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `<w:tblBorders>` — table-level borders.
 *
 * Upstream's `TableBorders` unconditionally emits all six sides (top,
 * left, bottom, right, insideH, insideV), filling any caller-omitted
 * side with `{ style: SINGLE, size: 4, color: "auto" }` (see
 * [BorderSide.UPSTREAM_DEFAULT]). We match byte-for-byte.
 *
 * Children are always emitted in the canonical order regardless of
 * construction order.
 */
internal class TableBorders(
    val top: BorderSide? = null,
    val left: BorderSide? = null,
    val bottom: BorderSide? = null,
    val right: BorderSide? = null,
    val insideH: BorderSide? = null,
    val insideV: BorderSide? = null,
) : XmlComponent("w:tblBorders") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:tblBorders")
        writeBorderSide(out, "w:top", top ?: BorderSide.UPSTREAM_DEFAULT)
        writeBorderSide(out, "w:left", left ?: BorderSide.UPSTREAM_DEFAULT)
        writeBorderSide(out, "w:bottom", bottom ?: BorderSide.UPSTREAM_DEFAULT)
        writeBorderSide(out, "w:right", right ?: BorderSide.UPSTREAM_DEFAULT)
        writeBorderSide(out, "w:insideH", insideH ?: BorderSide.UPSTREAM_DEFAULT)
        writeBorderSide(out, "w:insideV", insideV ?: BorderSide.UPSTREAM_DEFAULT)
        out.closeElement("w:tblBorders")
    }

    internal companion object {
        /**
         * The `<w:tblBorders>` block upstream emits when a table has no
         * caller-specified borders: six sides, all at
         * [BorderSide.UPSTREAM_DEFAULT].
         */
        val DEFAULTS: TableBorders = TableBorders()

        /**
         * The `TableBorders.NONE` shape from upstream — six sides each
         * with style=NONE, size=0, color="auto". Used by the
         * `noBorderTable` convenience to emit explicit "no borders"
         * markers (vs suppressing `<w:tblBorders>` entirely).
         */
        val NONE: TableBorders = run {
            val noneSide = BorderSide(
                style = io.docxkt.model.border.BorderStyle.NONE,
                size = 0,
                color = "auto",
                space = null,
            )
            TableBorders(
                top = noneSide,
                left = noneSide,
                bottom = noneSide,
                right = noneSide,
                insideH = noneSide,
                insideV = noneSide,
            )
        }
    }
}

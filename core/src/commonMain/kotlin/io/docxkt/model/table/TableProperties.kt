// Port of: src/file/table/table-properties/table-properties.ts
// (TableProperties, L94-L153).
package io.docxkt.model.table

import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.writeShading
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:tblPr>` — table properties.
 *
 * Upstream's full set spans tblStyle, float, bidiVisual, tblW,
 * jc, tblInd, tblBorders, shd, tblLayout, tblCellMar, tblLook,
 * cellSpacing. We cover tblStyle, tblW, tblInd, borders, shading,
 * tblLayout, cellMargins.
 *
 * Child order matches upstream's `properties.ts`:
 * `tblStyle → tblW → tblInd → tblBorders → shd → tblLayout →
 * tblCellMar`.
 *
 * [borders] mirrors upstream's "always emit six sides when table has
 * any borders" behaviour — see [TableBorders]. Our DSL defaults this
 * to [TableBorders.DEFAULTS] so call sites that don't set borders still
 * produce upstream-equivalent output; model-layer `null` = suppressed
 * remains available for direct-construction callers.
 */
internal class TableProperties(
    val styleReference: String? = null,
    val visuallyRightToLeft: Boolean? = null,
    val width: TableWidth? = null,
    val indent: TableWidth? = null,
    val layout: TableLayout? = null,
    val borders: TableBorders? = null,
    val shading: Shading? = null,
    val cellMargins: CellMargins? = null,
    val tableLook: TableLook? = null,
) : IgnoreIfEmptyXmlComponent("w:tblPr") {

    // Non-null cellMargins/borders are the DSL's contract that at least
    // one side is set; re-checking emptiness here would duplicate that.
    override fun isEmpty(): Boolean =
        styleReference == null && visuallyRightToLeft == null && width == null &&
            indent == null && layout == null && borders == null && shading == null &&
            cellMargins == null && tableLook == null

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:tblPr")
        styleReference?.let { out.selfClosingElement("w:tblStyle", "w:val" to it) }
        // bidiVisual emits as bare `<w:bidiVisual/>` when true (OOXML
        // OnOff convention); explicit `false` emits `<w:bidiVisual w:val="false"/>`.
        visuallyRightToLeft?.let {
            if (it) out.selfClosingElement("w:bidiVisual")
            else out.selfClosingElement("w:bidiVisual", "w:val" to "false")
        }
        width?.let { writeWidth(out, "w:tblW", it) }
        indent?.let { writeWidth(out, "w:tblInd", it) }
        borders?.appendXml(out)
        shading?.let { writeShading(out, it) }
        layout?.let { out.selfClosingElement("w:tblLayout", "w:type" to it.wire) }
        cellMargins?.let { writeCellMargins(out, "w:tblCellMar", it) }
        tableLook?.appendXml(out)
        out.closeElement("w:tblPr")
    }
}

// Port of: src/file/table/table-cell/table-cell-properties.ts
// (TableCellProperties, L144-L202).
package io.docxkt.model.table

import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.writeShading
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:tcPr>` — cell properties.
 *
 * Child order matches upstream's `table-cell-properties.ts`:
 * `tcW → gridSpan → vMerge → tcBorders → shd → tcMar → vAlign`. Text
 * direction and track-revision siblings are not yet supported.
 */
internal class TableCellProperties(
    val width: TableWidth? = null,
    val gridSpan: Int? = null,
    val verticalMerge: VerticalMerge? = null,
    val borders: TableCellBorders? = null,
    val shading: Shading? = null,
    val margins: CellMargins? = null,
    val textDirection: TextDirection? = null,
    val verticalAlign: VerticalAlignment? = null,
) : IgnoreIfEmptyXmlComponent("w:tcPr") {

    // A non-null `borders` or `margins` is the DSL's signal that the
    // caller set at least one side; re-checking emptiness here would
    // duplicate that contract. Direct-construction callers are trusted
    // to pass `null` when they mean "no borders / no margins".
    override fun isEmpty(): Boolean =
        width == null && gridSpan == null && verticalMerge == null &&
            borders == null && shading == null && margins == null &&
            textDirection == null && verticalAlign == null

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:tcPr")
        width?.let { writeWidth(out, "w:tcW", it) }
        gridSpan?.let { out.selfClosingElement("w:gridSpan", "w:val" to it.toString()) }
        verticalMerge?.let { out.selfClosingElement("w:vMerge", "w:val" to it.wire) }
        borders?.appendXml(out)
        shading?.let { writeShading(out, it) }
        margins?.let { writeCellMargins(out, "w:tcMar", it) }
        textDirection?.let { out.selfClosingElement("w:textDirection", "w:val" to it.wire) }
        verticalAlign?.let { out.selfClosingElement("w:vAlign", "w:val" to it.wire) }
        out.closeElement("w:tcPr")
    }
}

/**
 * Shared helper for `<w:tblW>` / `<w:tcW>` / `<w:tblInd>`. Attribute
 * order (`w:type, w:w`) matches upstream's BuilderElement and is
 * *significant for fixture diffs*.
 */
internal fun writeWidth(out: Appendable, elementName: String, width: TableWidth) {
    val sizeWire = if (width.type == WidthType.PCT) "${width.size}%" else width.size.toString()
    out.selfClosingElement(
        elementName,
        "w:type" to width.type.wire,
        "w:w" to sizeWire,
    )
}

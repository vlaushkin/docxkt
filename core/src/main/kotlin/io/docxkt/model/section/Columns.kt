// Port of: src/file/document/body/section-properties/properties/columns.ts
//          + column.ts.
package io.docxkt.model.section

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * One column definition inside `<w:cols>`. Public value type
 * supplied to [Columns.individual] when columns have unequal
 * widths.
 *
 * [widthTwips] is the column's content width; [spaceTwips] is
 * the gap that follows this column. `null` for [spaceTwips]
 * skips the attribute — upstream's `BuilderElement` skips
 * undefined values.
 */
public data class Column(
    val widthTwips: Int,
    val spaceTwips: Int? = null,
)

/**
 * `<w:cols>` — column layout for a section.
 *
 * - [count] (`w:num`) — number of columns. Required by our
 *   model; upstream defaults to 1 when omitted.
 * - [equalWidth] (`w:equalWidth`) — when true, columns share
 *   the space evenly. When false, [individual] supplies
 *   per-column widths.
 * - [spaceTwips] (`w:space`) — global gap between columns
 *   when equalWidth is true.
 * - [separator] (`w:sep`) — draw a vertical line between
 *   columns.
 * - [individual] — per-column children. Emitted as `<w:col>`
 *   elements only when not empty AND equalWidth != true.
 *
 * Pass-through: we don't validate `count > 45` (the OOXML
 * XSD limit) or `individual.size == count`.
 */
internal class Columns(
    val count: Int,
    val equalWidth: Boolean? = null,
    val spaceTwips: Int? = null,
    val separator: Boolean? = null,
    val individual: List<Column> = emptyList(),
) : XmlComponent("w:cols") {

    override fun appendXml(out: Appendable) {
        val attrs = arrayOf<Pair<String, String?>>(
            "w:space" to spaceTwips?.toString(),
            "w:num" to count.toString(),
            "w:sep" to separator?.toString(),
            "w:equalWidth" to equalWidth?.toString(),
        )
        // Children appear only when equalWidth is NOT true and we have any.
        val emitChildren = equalWidth != true && individual.isNotEmpty()
        if (emitChildren) {
            out.openElement("w:cols", *attrs)
            for (col in individual) {
                out.selfClosingElement(
                    "w:col",
                    "w:w" to col.widthTwips.toString(),
                    "w:space" to col.spaceTwips?.toString(),
                )
            }
            out.append("</w:cols>")
        } else {
            out.selfClosingElement("w:cols", *attrs)
        }
    }
}

// Port of: src/file/table/table-row/table-row-properties.ts
// (TableRowProperties, L108-L140).
package io.docxkt.model.table

import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.onOff
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:trPr>` — row properties.
 *
 * Upstream exposes `cantSplit`, `tableHeader`, `height`, `cellSpacing`,
 * plus track-revision siblings. We cover `tableHeader` (`w:tblHeader`
 * OnOff) and `height` (`w:trHeight` with optional `w:hRule`). Order
 * matches upstream: `tblHeader → trHeight`.
 */
internal class TableRowProperties(
    val tableHeader: Boolean? = null,
    val height: TableRowHeight? = null,
) : IgnoreIfEmptyXmlComponent("w:trPr") {

    override fun isEmpty(): Boolean = tableHeader == null && height == null

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:trPr")
        out.onOff("w:tblHeader", tableHeader)
        height?.let {
            out.selfClosingElement(
                "w:trHeight",
                "w:val" to it.value.toString(),
                "w:hRule" to it.rule?.wire,
            )
        }
        out.closeElement("w:trPr")
    }
}

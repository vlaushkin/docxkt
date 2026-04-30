// Port of: src/file/table/table-row/table-row.ts (L58-L66).
package io.docxkt.model.table

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * A `<w:tr>` row: an optional `<w:trPr>` followed by `<w:tc>` cells.
 */
internal class TableRow(
    val cells: List<TableCell>,
    val properties: TableRowProperties? = null,
) : XmlComponent("w:tr") {

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        properties?.appendXml(out)
        for (cell in cells) {
            cell.appendXml(out)
        }
        out.closeElement(elementName)
    }
}

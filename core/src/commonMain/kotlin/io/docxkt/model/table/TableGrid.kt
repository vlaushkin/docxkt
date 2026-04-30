// Port of: src/file/table/grid.ts (TableGrid + createGridCol).
package io.docxkt.model.table

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:tblGrid>` — one `<w:gridCol w:w="..."/>` per logical column.
 *
 * Required by OOXML even for a one-column table; upstream enforces a
 * non-empty list and so do we.
 */
internal class TableGrid(
    val columnWidthsTwips: List<Int>,
) : XmlComponent("w:tblGrid") {

    init {
        require(columnWidthsTwips.isNotEmpty()) {
            "TableGrid must carry at least one column width"
        }
    }

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        for (w in columnWidthsTwips) {
            out.selfClosingElement("w:gridCol", "w:w" to w.toString())
        }
        out.closeElement(elementName)
    }
}

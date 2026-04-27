// Port of: src/file/table/table.ts (L94-L162).
package io.docxkt.model.table

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `<w:tbl>` — the root of a table.
 *
 * OOXML requires the sequence `tblPr → tblGrid → rows*`. [TableGrid]
 * is non-optional (must carry at least one column); [properties] is
 * optional and suppressed when all fields are null.
 */
internal class Table(
    val properties: TableProperties?,
    val grid: TableGrid,
    val rows: List<TableRow>,
) : XmlComponent("w:tbl") {

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        properties?.appendXml(out)
        grid.appendXml(out)
        for (row in rows) {
            row.appendXml(out)
        }
        out.closeElement(elementName)
    }
}

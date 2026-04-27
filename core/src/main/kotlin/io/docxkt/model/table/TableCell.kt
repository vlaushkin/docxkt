// Port of: src/file/table/table-cell/table-cell.ts (L54-L72).
package io.docxkt.model.table

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * A `<w:tc>` cell: an optional `<w:tcPr>` followed by block-level
 * content. `children` is `List<XmlComponent>` so nested tables
 * (`<w:tbl>` inside `<w:tc>`) can sit alongside paragraphs.
 *
 * Empty properties collapse to nothing via [IgnoreIfEmptyXmlComponent];
 * empty `children` is permitted (callers pad with an empty paragraph
 * per the OOXML "cells must end in a paragraph" rule, but enforcement
 * lives at the DSL / API layer, not here).
 */
internal class TableCell(
    val children: List<XmlComponent>,
    val properties: TableCellProperties? = null,
) : XmlComponent("w:tc") {

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        properties?.appendXml(out)
        for (child in children) {
            child.appendXml(out)
        }
        out.closeElement(elementName)
    }
}

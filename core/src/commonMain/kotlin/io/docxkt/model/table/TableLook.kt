// Port of: src/file/table/table-properties/table-look.ts
package io.docxkt.model.table

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:tblLook>` — conditional-formatting hints for a table that uses
 * a styled tblStyle. Each non-null flag emits as `w:<name>="true"` /
 * `w:<name>="false"` (literal strings, NOT OnOff bare-element form —
 * matches upstream's `BuilderElement` attribute output).
 *
 * Attribute order: `firstRow → lastRow → firstColumn → lastColumn →
 * noHBand → noVBand` — upstream's BuilderElement push order.
 */
internal class TableLook(
    val firstRow: Boolean? = null,
    val lastRow: Boolean? = null,
    val firstColumn: Boolean? = null,
    val lastColumn: Boolean? = null,
    val noHBand: Boolean? = null,
    val noVBand: Boolean? = null,
) : XmlComponent("w:tblLook") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:tblLook",
            "w:firstRow" to firstRow?.toString(),
            "w:lastRow" to lastRow?.toString(),
            "w:firstColumn" to firstColumn?.toString(),
            "w:lastColumn" to lastColumn?.toString(),
            "w:noHBand" to noHBand?.toString(),
            "w:noVBand" to noVBand?.toString(),
        )
    }
}

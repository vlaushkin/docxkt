// Port of: src/file/numbering/abstract-numbering.ts.
package io.docxkt.model.numbering

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:abstractNum>` — a list-template definition.
 *
 * Upstream emits `w15:restartNumberingAfterBreak="0"` on every
 * abstract; we match. `<w:multiLevelType>` child is the first of
 * the abstract's children (upstream-canonical order).
 */
internal class AbstractNumbering(
    val abstractNumId: Int,
    val levels: List<NumberingLevel>,
    val multiLevelType: MultiLevelType = MultiLevelType.HYBRID_MULTILEVEL,
) : XmlComponent("w:abstractNum") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:abstractNum",
            "w:abstractNumId" to abstractNumId.toString(),
            "w15:restartNumberingAfterBreak" to "0",
        )
        out.selfClosingElement("w:multiLevelType", "w:val" to multiLevelType.wire)
        for (level in levels) {
            level.appendXml(out)
        }
        out.closeElement("w:abstractNum")
    }
}

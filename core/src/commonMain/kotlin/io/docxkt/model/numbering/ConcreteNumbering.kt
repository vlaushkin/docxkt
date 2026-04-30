// Port of: src/file/numbering/num.ts (ConcreteNumbering class).
package io.docxkt.model.numbering

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:num>` — a concrete instance of an [AbstractNumbering].
 *
 * Upstream always auto-emits a level-0 `<w:lvlOverride>` with a
 * `<w:startOverride w:val="1"/>` child. We match byte-for-byte so
 * goldens diff cleanly even when the user didn't ask for any
 * override.
 */
internal class ConcreteNumbering(
    val numId: Int,
    val abstractNumId: Int,
    val startOverride: Int = 1,
) : XmlComponent("w:num") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:num", "w:numId" to numId.toString())
        out.selfClosingElement("w:abstractNumId", "w:val" to abstractNumId.toString())
        // Upstream's auto-emitted level-0 startOverride.
        out.openElement("w:lvlOverride", "w:ilvl" to "0")
        out.selfClosingElement("w:startOverride", "w:val" to startOverride.toString())
        out.closeElement("w:lvlOverride")
        out.closeElement("w:num")
    }
}

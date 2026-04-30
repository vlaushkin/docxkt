// Port of: src/file/paragraph/run/simple-field.ts (SimpleField).
package io.docxkt.model.field

import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:fldSimple w:instr="…"/>` — a self-contained field element.
 *
 * When [cached] is null, emits a self-closed
 * `<w:fldSimple w:instr="…"/>`. When [cached] is set, wraps a
 * single `<w:r><w:t xml:space="preserve">…</w:t></w:r>` with the
 * cached display value.
 *
 * The instruction string is passed through `XmlEscape` — callers
 * hand `DATE \@ "MMM d, yyyy"` as a normal Kotlin string and the
 * quotes are escaped on the wire.
 *
 * Upstream's `SimpleField` admits richer cached content (any
 * paragraph-child subset); we currently support a single text run.
 */
internal class SimpleField(
    val instruction: String,
    val cached: String? = null,
) : XmlComponent("w:fldSimple") {

    override fun appendXml(out: Appendable) {
        if (cached == null) {
            out.selfClosingElement("w:fldSimple", "w:instr" to instruction)
        } else {
            out.openElement("w:fldSimple", "w:instr" to instruction)
            // Wrap cached display text in a single run.
            Run(children = listOf(Text(cached))).appendXml(out)
            out.closeElement("w:fldSimple")
        }
    }
}

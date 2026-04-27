// Port of: src/file/numbering/numbering.ts (Numbering root class).
package io.docxkt.part

import io.docxkt.model.numbering.AbstractNumbering
import io.docxkt.model.numbering.ConcreteNumbering
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `word/numbering.xml` — list-template definitions.
 *
 * Emits `<?xml standalone="yes" ?>` + `<w:numbering
 * mc:Ignorable="w14 w15 wp14" xmlns:wpc=... ...>` root with the
 * base 16-namespace set (upstream's narrower subset — same 16 used
 * on `<w:ftr>`, no `cx*` or `w16*`). Then every registered
 * abstract in `abstractNumId` order, then every concrete in
 * `numId` order.
 *
 * Emitted only when the document carries at least one list.
 */
internal class NumberingPart(
    val abstracts: List<AbstractNumbering>,
    val concretes: List<ConcreteNumbering>,
) {
    val path: String = "word/numbering.xml"

    val isNonEmpty: Boolean get() = abstracts.isNotEmpty() || concretes.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            add("mc:Ignorable" to Namespaces.DOCUMENT_MC_IGNORABLE)
            addAll(Namespaces.FOOTER_ROOT_NAMESPACES)
        }.toTypedArray()
        out.openElement("w:numbering", *attrs)
        for (a in abstracts) a.appendXml(out)
        for (c in concretes) c.appendXml(out)
        out.closeElement("w:numbering")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

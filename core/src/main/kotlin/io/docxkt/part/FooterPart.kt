// Port of: src/file/footer/footer.ts (part wrapper).
package io.docxkt.part

import io.docxkt.model.footer.Footer
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `word/footer{id}.xml` — a footer part.
 *
 * Emits `<?xml ...?>` + `<w:ftr xmlns...>` with the narrower footer
 * namespace set (see [Namespaces.FOOTER_ROOT_NAMESPACES]), then
 * paragraph children, then `</w:ftr>`.
 */
internal class FooterPart(
    val id: Int,
    val footer: Footer,
) {
    val path: String = "word/footer$id.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        val attrs = Namespaces.FOOTER_ROOT_NAMESPACES
            .toTypedArray<Pair<String, String?>>()
        out.openElement("w:ftr", *attrs)
        for (child in footer.children) child.appendXml(out)
        out.closeElement("w:ftr")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

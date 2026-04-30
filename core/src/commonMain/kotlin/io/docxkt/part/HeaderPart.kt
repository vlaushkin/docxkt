// Port of: src/file/header/header.ts (part wrapper — Header class
// emits the whole w:hdr root) + src/file/file.ts registration.
package io.docxkt.part

import io.docxkt.model.header.Header
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `word/header{id}.xml` — a header part.
 *
 * Emits `<?xml ...?>` + `<w:hdr xmlns...>` with the namespace set
 * upstream uses for headers (see
 * [Namespaces.HEADER_ROOT_NAMESPACES]), then the paragraph
 * children, then `</w:hdr>`.
 *
 * `id` drives the path (`word/header1.xml`, `word/header2.xml`, ...).
 */
internal class HeaderPart(
    val id: Int,
    val header: Header,
) {
    val path: String = "word/header$id.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        val attrs = Namespaces.HEADER_ROOT_NAMESPACES
            .toTypedArray<Pair<String, String?>>()
        out.openElement("w:hdr", *attrs)
        for (child in header.children) child.appendXml(out)
        out.closeElement("w:hdr")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

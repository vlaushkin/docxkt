// Port of: src/file/fonts/font-wrapper.ts (FontWrapper relationships).
package io.docxkt.part

import io.docxkt.api.EmbeddedFontWithRid
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/_rels/fontTable.xml.rels` — per-font binary relationships.
 *
 * Each entry points at `fonts/<name>.odttf` (the obfuscated TTF
 * binary). The relationship type is `.../officeDocument/2006/relationships/font`
 * — different from the document-scoped `fontTable` rel that lives
 * in `word/_rels/document.xml.rels`.
 */
internal class FontTableRelsPart(
    val fonts: List<EmbeddedFontWithRid>,
) {
    val path: String = "word/_rels/fontTable.xml.rels"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        out.openElement(
            "Relationships",
            "xmlns" to Namespaces.PACKAGE_RELATIONSHIPS,
        )
        for (entry in fonts) {
            out.selfClosingElement(
                "Relationship",
                "Id" to entry.rid,
                "Type" to DocumentRelTypes.FONT,
                "Target" to "fonts/${entry.font.name}.odttf",
            )
        }
        out.closeElement("Relationships")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

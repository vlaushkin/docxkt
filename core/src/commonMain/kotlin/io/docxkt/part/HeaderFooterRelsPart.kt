// Port of: src/file/document-wrapper.ts (Document.relationships,
//          per-header / per-footer rels writes — emitted alongside
//          their owning header*.xml / footer*.xml parts).
//
// Per-part relationships parts for headers and footers. Upstream
// emits these whenever a header/footer references an external
// resource (image, hyperlink, embedded object). Header/footer
// images get their own rId namespace inside
// `word/_rels/{header|footer}{idx}.xml.rels`.
package io.docxkt.part

import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/_rels/header{idx}.xml.rels` — relationships scoped to a
 * single header part. Same wire shape as [DocumentRelsPart] but
 * targets the header-relative path. Only emitted when the header
 * carries at least one rel.
 */
internal class HeaderRelsPart(
    val headerIndex: Int,
    val relationships: List<DocumentRelsPart.Relationship>,
) {
    val path: String = "word/_rels/header$headerIndex.xml.rels"

    val isNonEmpty: Boolean get() = relationships.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        out.openElement(
            "Relationships",
            "xmlns" to Namespaces.PACKAGE_RELATIONSHIPS,
        )
        for (rel in relationships) {
            out.selfClosingElement(
                "Relationship",
                "Id" to rel.id,
                "Type" to rel.type,
                "Target" to rel.target,
                "TargetMode" to rel.targetMode,
            )
        }
        out.closeElement("Relationships")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

/** Footer-side mirror of [HeaderRelsPart]. */
internal class FooterRelsPart(
    val footerIndex: Int,
    val relationships: List<DocumentRelsPart.Relationship>,
) {
    val path: String = "word/_rels/footer$footerIndex.xml.rels"

    val isNonEmpty: Boolean get() = relationships.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        out.openElement(
            "Relationships",
            "xmlns" to Namespaces.PACKAGE_RELATIONSHIPS,
        )
        for (rel in relationships) {
            out.selfClosingElement(
                "Relationship",
                "Id" to rel.id,
                "Type" to rel.type,
                "Target" to rel.target,
                "TargetMode" to rel.targetMode,
            )
        }
        out.closeElement("Relationships")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

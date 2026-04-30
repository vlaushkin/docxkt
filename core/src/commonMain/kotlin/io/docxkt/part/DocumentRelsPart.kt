// Port of: src/file/relationships/relationships.ts
// (document-scoped relationships part).
package io.docxkt.part

import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/_rels/document.xml.rels` — relationships originating at the
 * main document part. Holds `<w:headerReference>` /
 * `<w:footerReference>` targets, image / style / numbering rIds,
 * and any other document-scoped relationship.
 *
 * Not emitted at all when [relationships] is empty — a hello-world
 * `.docx` ships no document-level rels.
 */
internal class DocumentRelsPart(
    val relationships: List<Relationship>,
) {
    val path: String = "word/_rels/document.xml.rels"

    /** True when this part should be written to the ZIP. */
    val isNonEmpty: Boolean get() = relationships.isNotEmpty()

    /**
     * Optional fourth attribute `TargetMode` is `null` for every
     * internal relationship (headers, footers, numbering, styles,
     * images). External relationships (hyperlinks) set it to
     * `"External"`; the emitter omits the attribute entirely when
     * `null` so internal-only fixtures stay byte-identical.
     */
    data class Relationship(
        val id: String,
        val type: String,
        val target: String,
        val targetMode: String? = null,
    )

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

/** Known Open-Packaging relationship type URIs for document-scoped parts. */
internal object DocumentRelTypes {
    private const val BASE: String = Namespaces.RELATIONSHIPS_OFFICE_DOCUMENT
    const val HEADER: String = "$BASE/header"
    const val FOOTER: String = "$BASE/footer"
    const val NUMBERING: String = "$BASE/numbering"
    const val STYLES: String = "$BASE/styles"
    const val HYPERLINK: String = "$BASE/hyperlink"
    // Aux parts
    const val SETTINGS: String = "$BASE/settings"
    const val FONT_TABLE: String = "$BASE/fontTable"
    // Notes
    const val FOOTNOTES: String = "$BASE/footnotes"
    const val ENDNOTES: String = "$BASE/endnotes"
    // Comments
    const val COMMENTS: String = "$BASE/comments"
    // Embedded font binary
    const val FONT: String = "$BASE/font"
}

/** Known Open-Packaging relationship type URIs for package-scoped parts. */
internal object PackageRelTypes {
    private const val OFFICE_BASE: String = Namespaces.RELATIONSHIPS_OFFICE_DOCUMENT
    const val OFFICE_DOCUMENT: String = "$OFFICE_BASE/officeDocument"
    const val CORE_PROPERTIES: String = "${Namespaces.PACKAGE_RELATIONSHIPS_NAMESPACE}/metadata/core-properties"
    const val EXTENDED_PROPERTIES: String = "$OFFICE_BASE/extended-properties"
    const val CUSTOM_PROPERTIES: String = "$OFFICE_BASE/custom-properties"
}

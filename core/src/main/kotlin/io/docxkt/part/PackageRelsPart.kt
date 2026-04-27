// Port of: src/file/relationships/relationships.ts (package-level rels; the
// per-part rels live in the same upstream class parameterized differently).
package io.docxkt.part

import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `_rels/.rels` — the package-level relationships.
 *
 * Always carries the officeDocument rel (`word/document.xml`) and
 * may grow with metadata rels: `coreProperties` (docProps/core.xml),
 * `extendedProperties` (docProps/app.xml), `customProperties`
 * (docProps/custom.xml). The latter three are added conditionally
 * by `Document.assembleEntries()` based on what's active.
 */
internal class PackageRelsPart(
    val relationships: List<Relationship>,
) {
    val path: String = "_rels/.rels"

    data class Relationship(val id: String, val type: String, val target: String)

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
            )
        }
        out.closeElement("Relationships")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)

    companion object {
        /** Convenience — package rels with just the officeDocument entry. */
        fun default(): PackageRelsPart = PackageRelsPart(
            relationships = listOf(
                Relationship(
                    id = "rId1",
                    type = PackageRelTypes.OFFICE_DOCUMENT,
                    target = "word/document.xml",
                ),
            ),
        )
    }
}

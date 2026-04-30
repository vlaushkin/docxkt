// Port of: src/file/core-properties/properties.ts (CoreProperties).
package io.docxkt.part

import io.docxkt.model.metadata.CoreProperties
import io.docxkt.xml.Namespaces
import io.docxkt.xml.XmlEscape
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `docProps/core.xml` — Dublin-Core metadata. Always emitted on every
 * document. Emission order mirrors upstream's `CoreProperties`
 * constructor: optional elements (title, subject, creator, keywords,
 * description, lastModifiedBy, revision) first, then the two
 * timestamps unconditionally.
 *
 * Child element order in upstream's output when every field is set:
 * `dc:title → dc:subject → dc:creator → cp:keywords → dc:description
 * → cp:lastModifiedBy → cp:revision → dcterms:created → dcterms:modified`.
 *
 * Upstream's "Un-named" fallback for creator / lastModifiedBy is
 * applied at the [CoreProperties] value-type layer via the
 * `effective*` accessors.
 */
internal class CorePropertiesPart(
    val properties: CoreProperties,
) {
    val path: String = "docProps/core.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = Namespaces.CORE_PROPERTIES_NAMESPACES.toTypedArray<Pair<String, String?>>()
        out.openElement("cp:coreProperties", *attrs)
        properties.title?.let { appendStringElement(out, "dc:title", it) }
        properties.subject?.let { appendStringElement(out, "dc:subject", it) }
        appendStringElement(out, "dc:creator", properties.effectiveCreator)
        properties.keywords?.let { appendStringElement(out, "cp:keywords", it) }
        properties.description?.let { appendStringElement(out, "dc:description", it) }
        appendStringElement(out, "cp:lastModifiedBy", properties.effectiveLastModifiedBy)
        appendStringElement(out, "cp:revision", properties.effectiveRevision.toString())
        appendTimestamp(out, "dcterms:created", properties.createdAt)
        appendTimestamp(out, "dcterms:modified", properties.modifiedAt)
        out.closeElement("cp:coreProperties")
    }

    private fun appendStringElement(out: Appendable, name: String, value: String) {
        out.append('<')
        out.append(name)
        out.append('>')
        out.append(XmlEscape.escapeText(value))
        out.append("</")
        out.append(name)
        out.append('>')
    }

    private fun appendTimestamp(out: Appendable, name: String, value: String) {
        out.append('<')
        out.append(name)
        out.append(" xsi:type=\"dcterms:W3CDTF\">")
        out.append(XmlEscape.escapeText(value))
        out.append("</")
        out.append(name)
        out.append('>')
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

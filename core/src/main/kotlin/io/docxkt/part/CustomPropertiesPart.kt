// Port of: src/file/custom-properties/custom-properties.ts +
// custom-property.ts.
package io.docxkt.part

import io.docxkt.model.metadata.CustomProperty
import io.docxkt.xml.Namespaces
import io.docxkt.xml.XmlEscape
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `docProps/custom.xml` — user-provided key/value metadata pairs.
 *
 * `pid` counter starts at [CustomProperty.PID_START] (2) per
 * upstream's convention. Each entry emits:
 * `<property fmtid="{…}" pid="N" name="…"><vt:lpwstr>VAL</vt:lpwstr></property>`.
 *
 * When [entries] is empty this part is not emitted at all; the
 * assembly layer guards on [isNonEmpty].
 */
internal class CustomPropertiesPart(
    val entries: List<CustomProperty>,
) {
    val path: String = "docProps/custom.xml"

    val isNonEmpty: Boolean get() = entries.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        if (entries.isEmpty()) {
            out.selfClosingElement(
                "Properties",
                "xmlns" to Namespaces.CUSTOM_PROPERTIES,
                "xmlns:vt" to Namespaces.DOC_PROPS_VTYPES,
            )
            return
        }
        out.openElement(
            "Properties",
            "xmlns" to Namespaces.CUSTOM_PROPERTIES,
            "xmlns:vt" to Namespaces.DOC_PROPS_VTYPES,
        )
        entries.forEachIndexed { idx, entry ->
            val pid = CustomProperty.PID_START + idx
            out.append("<property fmtid=\"")
            out.append(CustomProperty.FORMAT_ID)
            out.append("\" pid=\"")
            out.append(pid.toString())
            out.append("\" name=\"")
            out.append(XmlEscape.escapeAttributeValue(entry.name))
            out.append("\"><vt:lpwstr>")
            out.append(XmlEscape.escapeText(entry.value))
            out.append("</vt:lpwstr></property>")
        }
        out.closeElement("Properties")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

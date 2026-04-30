// Port of: src/file/app-properties/app-properties.ts (AppProperties).
// Upstream emits an empty self-closed root with two namespace
// declarations; we do the same. Application / Company / Words and
// the rest of the body are not yet supported.
package io.docxkt.part

import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.selfClosingElement

internal class AppPropertiesPart {
    val path: String = "docProps/app.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        out.selfClosingElement(
            "Properties",
            "xmlns" to Namespaces.EXTENDED_PROPERTIES,
            "xmlns:vt" to Namespaces.DOC_PROPS_VTYPES,
        )
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

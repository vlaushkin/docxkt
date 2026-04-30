// Port of: src/file/content-types/content-types.ts
package io.docxkt.part

import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `[Content_Types].xml` — declares the MIME type for every part in
 * the package, via Default (by file extension) and Override (by
 * explicit path) entries.
 *
 * The main-document Override is emitted unconditionally. Additional
 * overrides (headers, footers, numbering, styles, …) are passed in
 * by the caller — each owning subsystem supplies the overrides its
 * parts require. Keeps the hello-world envelope minimal when no
 * extra parts are owned.
 */
internal class ContentTypesPart(
    val extraDefaults: List<Default> = emptyList(),
    val extraOverrides: List<Override> = emptyList(),
) {
    val path: String = "[Content_Types].xml"

    data class Default(val extension: String, val contentType: String)
    data class Override(val partName: String, val contentType: String)

    private val baseDefaults: List<Default> = listOf(
        Default("rels", CONTENT_TYPE_RELS),
        Default("xml", CONTENT_TYPE_XML),
    )

    private val baseOverride: Override =
        Override("/word/document.xml", CONTENT_TYPE_MAIN_DOCUMENT)

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = false)
        out.openElement("Types", "xmlns" to Namespaces.PACKAGE_CONTENT_TYPES)
        // Image-extension defaults (when present) emit BEFORE the base
        // rels/xml defaults — upstream's order
        // (image/png, image/jpeg, ..., rels, xml).
        for (d in extraDefaults) {
            out.selfClosingElement(
                "Default",
                "ContentType" to d.contentType,
                "Extension" to d.extension,
            )
        }
        for (d in baseDefaults) {
            out.selfClosingElement(
                "Default",
                "ContentType" to d.contentType,
                "Extension" to d.extension,
            )
        }
        out.selfClosingElement(
            "Override",
            "ContentType" to baseOverride.contentType,
            "PartName" to baseOverride.partName,
        )
        for (o in extraOverrides) {
            out.selfClosingElement(
                "Override",
                "ContentType" to o.contentType,
                "PartName" to o.partName,
            )
        }
        out.closeElement("Types")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()

    companion object {
        const val CONTENT_TYPE_RELS: String =
            "application/vnd.openxmlformats-package.relationships+xml"
        const val CONTENT_TYPE_XML: String = "application/xml"
        const val CONTENT_TYPE_MAIN_DOCUMENT: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document.main+xml"
        const val CONTENT_TYPE_HEADER: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.header+xml"
        const val CONTENT_TYPE_FOOTER: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.footer+xml"
        const val CONTENT_TYPE_NUMBERING: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.numbering+xml"
        const val CONTENT_TYPE_STYLES: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.styles+xml"
        // Metadata & auxiliary parts
        const val CONTENT_TYPE_CORE_PROPERTIES: String =
            "application/vnd.openxmlformats-package.core-properties+xml"
        const val CONTENT_TYPE_EXTENDED_PROPERTIES: String =
            "application/vnd.openxmlformats-officedocument.extended-properties+xml"
        const val CONTENT_TYPE_CUSTOM_PROPERTIES: String =
            "application/vnd.openxmlformats-officedocument.custom-properties+xml"
        const val CONTENT_TYPE_SETTINGS: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.settings+xml"
        const val CONTENT_TYPE_FONT_TABLE: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.fontTable+xml"
        // Notes
        const val CONTENT_TYPE_FOOTNOTES: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.footnotes+xml"
        const val CONTENT_TYPE_ENDNOTES: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.endnotes+xml"
        // Comments
        const val CONTENT_TYPE_COMMENTS: String =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.comments+xml"
        // Embedded font binary
        const val CONTENT_TYPE_OBFUSCATED_FONT: String =
            "application/vnd.openxmlformats-officedocument.obfuscatedFont"
    }
}

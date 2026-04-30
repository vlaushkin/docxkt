// Port of: src/patcher/content-types-manager.ts
package io.docxkt.patcher.replace

import io.docxkt.xml.Namespaces
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.childNodes
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.localName

/**
 * Manipulates `[Content_Types].xml` — the part-list manifest. The
 * file's root is `<Types>`; children are `<Default Extension=
 * "…" ContentType="…"/>` and `<Override PartName="…" ContentType=
 * "…"/>` elements.
 *
 * Currently only handles `<Default>` additions for image
 * extensions (one per format used).
 */
internal object ContentTypesManager {

    /**
     * Add a `<Default Extension=… ContentType=…>` entry if not
     * already present. Dedupe key: `(extension, contentType)` pair.
     */
    fun addDefaultExtension(doc: Document, extension: String, contentType: String) {
        val root = doc.documentElement!!
        // Walk existing <Default> children; bail if a matching one
        // already exists.
        val children = root.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i) ?: continue
            if (n !is Element) continue
            if (n.localName == "Default" &&
                n.getAttribute("Extension") == extension &&
                n.getAttribute("ContentType") == contentType
            ) {
                return  // already present
            }
        }
        // Append a new <Default>. Use the package content-types
        // namespace so the element matches the existing siblings.
        val newDefault = doc.createElementNS(Namespaces.PACKAGE_CONTENT_TYPES_NAMESPACE, "Default")
        newDefault.setAttribute("Extension", extension)
        newDefault.setAttribute("ContentType", contentType)
        root.appendChild(newDefault)
    }
}

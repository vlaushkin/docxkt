// Port of: src/file/document/document.ts +
//          src/file/document/document-attributes.ts
package io.docxkt.part

import io.docxkt.model.Body
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/document.xml` — the main body part of a wordprocessingML document.
 *
 * Emits the `<w:document>` root with `mc:Ignorable` and the full set of
 * `xmlns:*` namespaces upstream declares (see
 * [Namespaces.DOCUMENT_ROOT_NAMESPACES]). Namespaces unused by simple
 * documents (drawing, math, charts, …) are kept anyway to match
 * upstream's wire and keep fixture diffs clean.
 */
internal class MainDocumentPart(
    val body: Body,
    /** Document background color (hex RGB or "auto"). Emits
     * `<w:background w:color="…"/>` before `<w:body>`. */
    val backgroundColor: String? = null,
) {
    val path: String = "word/document.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val rootAttrs = buildList<Pair<String, String?>> {
            add("mc:Ignorable" to Namespaces.DOCUMENT_MC_IGNORABLE)
            addAll(Namespaces.DOCUMENT_ROOT_NAMESPACES)
        }.toTypedArray()
        out.openElement("w:document", *rootAttrs)
        backgroundColor?.let {
            out.selfClosingElement("w:background", "w:color" to it)
        }
        body.appendXml(out)
        out.closeElement("w:document")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

// Port of: src/patcher/from-docx.ts (XML output via xml-js's js2xml).
// Hand-rolled DOM walker so we preserve source-order xmlns
// attributes, retain `xml:space="preserve"`, and emit self-closing
// `<x/>` (matches upstream's xml-js style).
package io.docxkt.patcher.io

import org.w3c.dom.Attr
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Serializes a `org.w3c.dom.Document` back to XML bytes.
 *
 * Output configuration:
 * - UTF-8 encoding.
 * - `standalone="yes"` — matches upstream's prelude for OOXML
 *   parts.
 * - Single-line output (no pretty-printing).
 * - Attributes emitted in NamedNodeMap iteration order, which the
 *   JDK preserves from the parse if the element wasn't modified.
 * - Empty elements emit `<x/>`, not `<x></x>` — matches xml-js.
 */
internal object OoxmlWriter {

    /** Serialize [doc] to a UTF-8 byte array. */
    internal fun serialize(doc: Document): ByteArray {
        val sb = StringBuilder()
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
        val root = doc.documentElement
        if (root != null) writeElement(root, sb)
        return sb.toString().toByteArray(Charsets.UTF_8)
    }

    private fun writeElement(element: Element, sb: StringBuilder) {
        sb.append('<').append(element.tagName)
        // Prefer the parser-stashed source order. The DOM
        // NamedNodeMap alphabetises `xmlns:*` declarations, which
        // breaks byte-equal round-trip. When the element was created
        // post-parse (no user data), fall through to NamedNodeMap.
        @Suppress("UNCHECKED_CAST")
        val sourceOrder = element.getUserData(SOURCE_ATTR_ORDER_KEY)
            as? List<Pair<String, String>>
        if (sourceOrder != null && sourceOrder.size == element.attributes.length) {
            for ((name, value) in sourceOrder) {
                sb.append(' ').append(name).append('=').append('"')
                sb.append(escapeAttr(value))
                sb.append('"')
            }
        } else {
            val attrs = element.attributes
            for (i in 0 until attrs.length) {
                val a = attrs.item(i) as Attr
                sb.append(' ').append(a.name).append('=').append('"')
                sb.append(escapeAttr(a.value))
                sb.append('"')
            }
        }
        // Empty element → self-close, except <w:t> with xml:space="preserve"
        // — that one Word treats specially (stripping the empty text
        // would lose the explicit-empty signal). For consistency with
        // upstream, self-close any element with NO children regardless.
        val children = element.childNodes
        if (children.length == 0) {
            sb.append("/>")
            return
        }
        sb.append('>')
        for (i in 0 until children.length) {
            val n = children.item(i)
            when (n.nodeType) {
                Node.ELEMENT_NODE -> writeElement(n as Element, sb)
                Node.TEXT_NODE -> sb.append(escapeText((n as org.w3c.dom.Text).data))
                Node.CDATA_SECTION_NODE -> {
                    sb.append("<![CDATA[")
                    sb.append((n as org.w3c.dom.CDATASection).data)
                    sb.append("]]>")
                }
                Node.COMMENT_NODE -> {
                    sb.append("<!--")
                    sb.append((n as org.w3c.dom.Comment).data)
                    sb.append("-->")
                }
                Node.PROCESSING_INSTRUCTION_NODE -> {
                    val pi = n as org.w3c.dom.ProcessingInstruction
                    sb.append("<?").append(pi.target).append(' ').append(pi.data).append("?>")
                }
                // Skip everything else (entity references etc.)
            }
        }
        sb.append("</").append(element.tagName).append('>')
    }

    private fun escapeAttr(value: String): String {
        val sb = StringBuilder(value.length)
        for (c in value) {
            when (c) {
                '&' -> sb.append("&amp;")
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                '"' -> sb.append("&quot;")
                '\t' -> sb.append("&#9;")
                '\n' -> sb.append("&#10;")
                '\r' -> sb.append("&#13;")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }

    private fun escapeText(value: String): String {
        val sb = StringBuilder(value.length)
        for (c in value) {
            when (c) {
                '&' -> sb.append("&amp;")
                '<' -> sb.append("&lt;")
                '>' -> sb.append("&gt;")
                else -> sb.append(c)
            }
        }
        return sb.toString()
    }
}

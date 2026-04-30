// Port of: src/patcher/from-docx.ts (XML output via xml-js's js2xml).
// Hand-walks the parsed `dom2.Document` and emits each element via
// `StringBuilder`, consulting [AttrSourceOrder] for namespace
// declaration / attribute source order. xmlutil's `DomReader`
// alphabetises `xmlns:*` declarations (it inherits the platform
// DOM's NamedNodeMap iteration order), and its `KtXmlWriter`
// rejects xmlns decls that conflict with what `startTag` has
// already auto-bound — both behaviours break the byte-equal
// round-trip the patcher fixture battery relies on. We therefore
// bypass xmlutil's writer entirely on the emission path.
package io.docxkt.patcher.io

import nl.adaptivity.xmlutil.dom2.CDATASection
import nl.adaptivity.xmlutil.dom2.Comment
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.ProcessingInstruction
import nl.adaptivity.xmlutil.dom2.Text
import nl.adaptivity.xmlutil.dom2.attributes
import nl.adaptivity.xmlutil.dom2.childNodes
import nl.adaptivity.xmlutil.dom2.data
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.localName
import nl.adaptivity.xmlutil.dom2.name
import nl.adaptivity.xmlutil.dom2.prefix
import nl.adaptivity.xmlutil.dom2.target
import nl.adaptivity.xmlutil.dom2.value

/**
 * Serializes an xmlutil [Document] back to XML bytes.
 *
 * Output configuration:
 * - UTF-8 encoding.
 * - `<?xml version="1.0" encoding="UTF-8" standalone="yes"?>`
 *   prelude unconditionally (matches upstream's OOXML-part
 *   header).
 * - Single-line output (no pretty-printing).
 * - Empty elements emit `<x/>` — matches xml-js / upstream.
 * - Attributes emitted in [AttrSourceOrder] order when present
 *   (parser-stashed source order); else NamedNodeMap iteration
 *   order (only happens for elements created post-parse, where
 *   the XMLUnit byNameAndAllAttributes diff is order-insensitive).
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
        val tagName = qname(element)
        sb.append('<').append(tagName)
        emitAttributes(element, sb)
        val children = element.childNodes
        if (children.length == 0) {
            sb.append("/>")
            return
        }
        sb.append('>')
        for (i in 0 until children.length) {
            when (val c = children.item(i) ?: continue) {
                is Element -> writeElement(c, sb)
                is CDATASection -> sb.append("<![CDATA[").append(c.data).append("]]>")
                is Text -> sb.append(escapeText(c.data))
                is Comment -> sb.append("<!--").append(c.data).append("-->")
                is ProcessingInstruction -> {
                    sb.append("<?").append(c.target)
                    val data = c.data
                    if (!data.isNullOrEmpty()) sb.append(' ').append(data)
                    sb.append("?>")
                }
                else -> { /* skip unknown node types */ }
            }
        }
        sb.append("</").append(tagName).append('>')
    }

    private fun emitAttributes(element: Element, sb: StringBuilder) {
        val sourceOrder = AttrSourceOrder.get(element)
        if (sourceOrder == null) {
            for (attr in element.attributes) {
                appendAttr(sb, attr.name, attr.value)
            }
            return
        }
        // Merge: emit each AttrSourceOrder entry in source order,
        // sourcing the current value from the DOM (so post-parse
        // mutations like xml:space propagation see their effect).
        // Then append any DOM attributes not in the source-order
        // list. This fallback path is what catches the
        // setAttributeNS calls the replacers perform after parse.
        val emittedNames = HashSet<String>()
        for ((name, _) in sourceOrder) {
            val current = currentAttributeValue(element, name)
            if (current != null) {
                appendAttr(sb, name, current)
                emittedNames += name
            }
        }
        for (attr in element.attributes) {
            if (attr.name in emittedNames) continue
            appendAttr(sb, attr.name, attr.value)
        }
    }

    private fun appendAttr(sb: StringBuilder, name: String, value: String) {
        sb.append(' ').append(name).append('=').append('"')
        sb.append(escapeAttr(value))
        sb.append('"')
    }

    /**
     * Look up [name]'s current value on [element]. Iterates the
     * NamedNodeMap because `getAttribute` collapses unset to ""
     * (which would mask deletions).
     */
    private fun currentAttributeValue(element: Element, name: String): String? {
        for (attr in element.attributes) {
            if (attr.name == name) return attr.value
        }
        return null
    }

    private fun qname(element: Element): String {
        val pre = element.prefix.orEmpty()
        val local = element.localName.orEmpty()
        return if (pre.isEmpty()) local else "$pre:$local"
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

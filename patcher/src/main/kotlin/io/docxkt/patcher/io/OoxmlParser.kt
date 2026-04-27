// Port of: src/patcher/util.ts (toJson — XML to tree). Upstream uses
// xml-js for JS object trees; we use a StAX-based reader that builds
// a `org.w3c.dom.Document` while preserving the source's namespace
// declaration order, `xml:space="preserve"`, and duplicate attribute
// values. The default JDK DocumentBuilder reorganises all of those
// during parse, breaking byte-equal round-trip.
//
// The DOM remains the IR (downstream code uses NS-aware DOM APIs:
// `getElementsByTagNameNS`, `createElementNS`, `namespaceURI`,
// `localName`). Only the parse-time construction is hand-rolled.
//
// The parser also stashes the SOURCE attribute order on each Element
// as user-data under [SOURCE_ATTR_ORDER_KEY]. The companion writer
// (`OoxmlWriter`) prefers that list over the JDK NamedNodeMap which
// alphabetises `xmlns:*` declarations.
package io.docxkt.patcher.io

import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.ByteArrayInputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants

/**
 * User-data key carrying the source-order list of `(qname, value)`
 * pairs for an element's attributes (xmlns declarations + regular
 * attributes, in the order they appeared in the input). The writer
 * iterates this list — when present — to preserve byte-equal round
 * trip; otherwise it falls back to NamedNodeMap iteration.
 */
internal const val SOURCE_ATTR_ORDER_KEY: String = "io.docxkt.patcher.io.sourceAttrOrder"

/**
 * Parses an OOXML part's XML bytes into a `org.w3c.dom.Document`.
 *
 * Uses StAX (`javax.xml.stream.XMLStreamReader`) for the actual
 * parse, then constructs DOM nodes element-by-element. Compared
 * to the JDK DocumentBuilder, this guarantees:
 *
 * - **Namespace-attribute order is preserved.** `<w:document xmlns:w
 *   xmlns:r xmlns:m ...>` keeps the source order; the JDK alphabetises
 *   it.
 * - **`xml:space="preserve"` is preserved** on text-bearing elements
 *   (`<w:t>`, `<w:instrText>`, etc.). The JDK silently drops it.
 * - **Duplicate attribute values are preserved.** The JDK would dedupe
 *   `mc:Ignorable="w14 w15 wp14"` to `"w14 wp14"` (treats `w15` as
 *   duplicate of something it conflates with). We pass values through
 *   verbatim.
 *
 * Schema validation (XSD) is NOT performed. Malformed XML throws an
 * [javax.xml.stream.XMLStreamException].
 */
internal object OoxmlParser {

    private val xmlInputFactory: XMLInputFactory = XMLInputFactory.newInstance().apply {
        setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, true)
        setProperty(XMLInputFactory.IS_COALESCING, true)
        setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true)
        // XXE hardening — disable DOCTYPE entirely.
        setProperty(XMLInputFactory.SUPPORT_DTD, false)
        setProperty("javax.xml.stream.isSupportingExternalEntities", false)
    }

    private val docBuilderFactory: DocumentBuilderFactory =
        DocumentBuilderFactory.newInstance().apply {
            isNamespaceAware = true
            // XXE hardening — defensive; we don't actually parse with
            // this builder, only use it to produce empty DOMs.
            setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)
        }

    /** Parse [bytes] as XML and return the resulting DOM document. */
    internal fun parse(bytes: ByteArray): Document {
        val doc = docBuilderFactory.newDocumentBuilder().newDocument()
        val reader = xmlInputFactory.createXMLStreamReader(ByteArrayInputStream(bytes))
        try {
            // Stack of currently-open elements; root sits at index 0
            // once we've encountered START_ELEMENT for the document
            // root.
            val stack = ArrayDeque<Element>()

            while (reader.hasNext()) {
                when (reader.next()) {
                    XMLStreamConstants.START_ELEMENT -> {
                        val element = createElement(doc, reader)
                        if (stack.isEmpty()) {
                            doc.appendChild(element)
                        } else {
                            stack.last().appendChild(element)
                        }
                        stack.addLast(element)
                    }
                    XMLStreamConstants.END_ELEMENT -> {
                        stack.removeLast()
                    }
                    XMLStreamConstants.CHARACTERS,
                    XMLStreamConstants.SPACE,
                    XMLStreamConstants.ENTITY_REFERENCE -> {
                        if (stack.isEmpty()) continue
                        val text = reader.text
                        if (text.isNotEmpty()) {
                            stack.last().appendChild(doc.createTextNode(text))
                        }
                    }
                    XMLStreamConstants.CDATA -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(doc.createCDATASection(reader.text))
                    }
                    XMLStreamConstants.COMMENT -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(doc.createComment(reader.text))
                    }
                    XMLStreamConstants.PROCESSING_INSTRUCTION -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(
                            doc.createProcessingInstruction(reader.piTarget, reader.piData ?: "")
                        )
                    }
                    XMLStreamConstants.DTD -> {
                        // XXE hardening — refuse any document carrying a
                        // DOCTYPE declaration.
                        throw javax.xml.stream.XMLStreamException(
                            "DOCTYPE declaration is not permitted in OOXML parts",
                        )
                    }
                    // START_DOCUMENT / END_DOCUMENT: ignore.
                }
            }
        } finally {
            reader.close()
        }
        return doc
    }

    /**
     * Build a single DOM Element from the StAX reader's current
     * START_ELEMENT event. Namespace declarations come first (in
     * source order), then regular attributes (in source order).
     * Both kinds round-trip verbatim through the writer.
     */
    private fun createElement(
        doc: Document,
        reader: javax.xml.stream.XMLStreamReader,
    ): Element {
        val prefix = reader.prefix.orEmpty()
        val localName = reader.localName
        val qname = if (prefix.isEmpty()) localName else "$prefix:$localName"
        val uri = reader.namespaceURI
        val element = if (!uri.isNullOrEmpty()) {
            doc.createElementNS(uri, qname)
        } else {
            doc.createElement(qname)
        }
        // Source-order attribute list: namespace declarations first
        // (in StAX-reported order), then regular attributes (in
        // StAX-reported order). The JDK DOM's NamedNodeMap silently
        // alphabetises xmlns:* declarations, so the writer must use
        // this list — not the NamedNodeMap — to round-trip cleanly.
        val sourceOrder = ArrayList<Pair<String, String>>(
            reader.namespaceCount + reader.attributeCount,
        )
        // Namespace declarations on this element, in source order.
        // StAX exposes these via getNamespaceCount/getNamespacePrefix
        // /getNamespaceURI rather than as regular attributes.
        for (i in 0 until reader.namespaceCount) {
            val nsPrefix = reader.getNamespacePrefix(i).orEmpty()
            val nsUri = reader.getNamespaceURI(i).orEmpty()
            val attrName = if (nsPrefix.isEmpty()) "xmlns" else "xmlns:$nsPrefix"
            element.setAttribute(attrName, nsUri)
            sourceOrder += attrName to nsUri
        }
        // Regular attributes in source order. xml:space lives in the
        // XML namespace; setAttributeNS with that URI preserves it.
        for (i in 0 until reader.attributeCount) {
            val aPrefix = reader.getAttributePrefix(i).orEmpty()
            val aLocal = reader.getAttributeLocalName(i)
            val aQname = if (aPrefix.isEmpty()) aLocal else "$aPrefix:$aLocal"
            val aUri = reader.getAttributeNamespace(i)
            val aValue = reader.getAttributeValue(i)
            if (!aUri.isNullOrEmpty()) {
                element.setAttributeNS(aUri, aQname, aValue)
            } else {
                element.setAttribute(aQname, aValue)
            }
            sourceOrder += aQname to aValue
        }
        element.setUserData(SOURCE_ATTR_ORDER_KEY, sourceOrder, null)
        return element
    }
}

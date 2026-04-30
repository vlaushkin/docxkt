// Port of: src/patcher/util.ts (toJson — XML to tree). Upstream uses
// xml-js for JS object trees; we drive pdvrieze/xmlutil's streaming
// reader directly into an `nl.adaptivity.xmlutil.dom2` document.
//
// Pre-migration this used a StAX-based reader plus the JDK
// `DocumentBuilder` and stashed source-order attribute lists on each
// element via DOM user-data so the writer could undo the platform
// DOM's alphabetisation of namespace declarations.
//
// xmlutil's DOM (a `WrappingNamedNodeMap` over the platform DOM)
// has the same alphabetisation behaviour for namespace
// declarations, so we keep an out-of-band side channel — but as a
// regular `HashMap<Element, ...>`, since `dom2.Node` does not
// expose user-data getters/setters and xmlutil wraps every node
// access in a fresh wrapper instance (so identity hashing is not
// usable here).
package io.docxkt.patcher.io

import nl.adaptivity.xmlutil.EventType
import nl.adaptivity.xmlutil.XmlReader
import nl.adaptivity.xmlutil.XmlUtilInternal
import nl.adaptivity.xmlutil.dom2.CDATASection
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.Text
import nl.adaptivity.xmlutil.dom2.data
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.lastChild
import nl.adaptivity.xmlutil.newGenericReader
import nl.adaptivity.xmlutil.xmlStreaming
import java.io.ByteArrayInputStream

/**
 * Side-channel mapping each parsed [Element] to the source-order
 * list of `(qname, value)` attribute pairs (xmlns declarations
 * first, then regular attributes, in source order). [OoxmlWriter]
 * iterates this list when serializing, instead of the DOM's
 * `NamedNodeMap` (which alphabetises `xmlns:*`).
 *
 * Entries are added during [OoxmlParser.parse]. xmlutil's `dom2`
 * facade wraps the platform DOM nodes in a fresh wrapper on every
 * accessor call, so `IdentityHashMap` would lose entries — we use
 * a regular `HashMap` and rely on `dom2.Element`'s equals/hashCode
 * delegating to the underlying platform node's identity.
 */
internal object AttrSourceOrder {
    private val map = HashMap<Element, List<Pair<String, String>>>()

    @Synchronized
    internal fun put(element: Element, list: List<Pair<String, String>>) {
        map[element] = list
    }

    @Synchronized
    internal fun get(element: Element): List<Pair<String, String>>? = map[element]

    @Synchronized
    internal fun forget(element: Element) {
        map.remove(element)
    }
}

/**
 * Parses an OOXML part's XML bytes into an xmlutil
 * [nl.adaptivity.xmlutil.dom2.Document].
 *
 * Schema validation (XSD) is NOT performed. Malformed XML throws
 * an `nl.adaptivity.xmlutil.XmlException`. DOCTYPE declarations
 * are refused for XXE hardening — even non-validating readers
 * can be coerced into expanding entity references that point at
 * host resources, so we treat any DOCDECL event as a hard error.
 */
internal object OoxmlParser {

    /**
     * Parse [bytes] as XML and return the resulting DOM document.
     *
     * `DomWriter.target` is annotated `@XmlUtilInternal` (xmlutil
     * does not stabilise that internal field across releases). We
     * opt in: the target is exactly the empty document we use as
     * the construction substrate; no other API offers a fresh
     * empty `dom2.Document` on the JVM.
     */
    @OptIn(XmlUtilInternal::class)
    internal fun parse(bytes: ByteArray): Document {
        // The platform (StAX-backed) reader on JVM alphabetises
        // namespace declarations exposed via `namespaceDecls`.
        // xmlutil's pure-Kotlin generic reader preserves source
        // order, which the patcher relies on to round-trip
        // `<w:document>`'s 30+ namespace attributes.
        val reader = xmlStreaming.newGenericReader(ByteArrayInputStream(bytes))
        try {
            // xmlutil's `genericDomImplementation` (the platform
            // DOM on JVM) requires a non-empty qualified name for
            // `createDocument`, so we seed the doc with a placeholder
            // root and immediately remove it. The remaining empty
            // document is the construction substrate for the manual
            // tree build below.
            val doc = xmlStreaming.genericDomImplementation
                .createDocument("urn:placeholder", "placeholder", null)
            val placeholder = doc.documentElement
            if (placeholder != null) doc.removeChild(placeholder)
            val stack = ArrayDeque<Element>()

            while (reader.hasNext()) {
                when (reader.next()) {
                    EventType.DOCDECL ->
                        throw IllegalArgumentException(
                            "DOCTYPE declaration is not permitted in OOXML parts",
                        )
                    EventType.START_ELEMENT -> {
                        val element = createElement(doc, reader)
                        if (stack.isEmpty()) {
                            doc.appendChild(element)
                        } else {
                            stack.last().appendChild(element)
                        }
                        stack.addLast(element)
                    }
                    EventType.END_ELEMENT -> {
                        stack.removeLast()
                    }
                    EventType.TEXT,
                    EventType.IGNORABLE_WHITESPACE,
                    EventType.ENTITY_REF -> {
                        if (stack.isEmpty()) continue
                        val text = reader.text
                        if (text.isEmpty()) continue
                        appendText(doc, stack.last(), text)
                    }
                    EventType.CDSECT -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(doc.createCDATASection(reader.text))
                    }
                    EventType.COMMENT -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(doc.createComment(reader.text))
                    }
                    EventType.PROCESSING_INSTRUCTION -> {
                        if (stack.isEmpty()) continue
                        stack.last().appendChild(
                            doc.createProcessingInstruction(reader.piTarget, reader.piData ?: ""),
                        )
                    }
                    EventType.START_DOCUMENT,
                    EventType.END_DOCUMENT,
                    EventType.ATTRIBUTE -> {
                        // No-op: attributes ride START_ELEMENT;
                        // document boundaries don't materialise as
                        // DOM nodes.
                    }
                }
            }
            return doc
        } finally {
            reader.close()
        }
    }

    /**
     * Build a single DOM Element from the reader's current
     * START_ELEMENT event. Namespace declarations come first (in
     * source order), then regular attributes (in source order).
     * Both kinds round-trip verbatim through [OoxmlWriter] thanks
     * to [AttrSourceOrder].
     */
    private fun createElement(doc: Document, reader: XmlReader): Element {
        val prefix = reader.prefix.orEmpty()
        val localName = reader.localName
        val qname = if (prefix.isEmpty()) localName else "$prefix:$localName"
        val uri = reader.namespaceURI
        val element = if (uri.isNotEmpty()) {
            doc.createElementNS(uri, qname)
        } else {
            doc.createElement(qname)
        }
        val nsCount = reader.namespaceDecls.size
        val sourceOrder = ArrayList<Pair<String, String>>(nsCount + reader.attributeCount)
        // Namespace declarations on this element, in source order
        // (the generic KtXmlReader preserves the order we want).
        for (ns in reader.namespaceDecls) {
            val nsPrefix = ns.prefix
            val nsUri = ns.namespaceURI
            val attrName = if (nsPrefix.isEmpty()) "xmlns" else "xmlns:$nsPrefix"
            element.setAttribute(attrName, nsUri)
            sourceOrder += attrName to nsUri
        }
        // Regular attributes in source order. xml:space lives in
        // the XML namespace; setAttributeNS with that URI preserves
        // the qualified form.
        for (i in 0 until reader.attributeCount) {
            val aPrefix = reader.getAttributePrefix(i).orEmpty()
            val aLocal = reader.getAttributeLocalName(i)
            val aQname = if (aPrefix.isEmpty()) aLocal else "$aPrefix:$aLocal"
            val aUri = reader.getAttributeNamespace(i)
            val aValue = reader.getAttributeValue(i)
            // Some readers (StAX-backed) surface `xmlns:*`
            // declarations as regular attributes too. Skip them —
            // `namespaceDecls` is the canonical source.
            if (aQname == "xmlns" || aQname.startsWith("xmlns:")) continue
            if (!aUri.isNullOrEmpty()) {
                element.setAttributeNS(aUri, aQname, aValue)
            } else {
                element.setAttribute(aQname, aValue)
            }
            sourceOrder += aQname to aValue
        }
        AttrSourceOrder.put(element, sourceOrder)
        return element
    }

    /**
     * Append [text] to [parent], coalescing with the existing
     * trailing `Text` child if one exists. xmlutil emits a TEXT
     * event per entity boundary (`Hello &lt;%name%&gt;!` arrives
     * as five TEXT events with the entity-decoded characters in
     * between), so we glue them into a single `Text` node here.
     * The patcher's marker scanner expects each `<w:t>` to carry
     * one contiguous run of text per sibling group.
     */
    private fun appendText(doc: Document, parent: Element, text: String) {
        val last = parent.lastChild
        if (last is Text && last !is CDATASection) {
            last.data = last.data + text
        } else {
            parent.appendChild(doc.createTextNode(text))
        }
    }
}

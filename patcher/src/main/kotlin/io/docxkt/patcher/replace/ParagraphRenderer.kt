// No upstream analogue — shared paragraph-rendering helpers used by
// TokenReplacer, ParagraphInjector, ImageInjector, RowInjector, and
// ParagraphInlineReplacer.
package io.docxkt.patcher.replace

import io.docxkt.xml.Namespaces
import nl.adaptivity.xmlutil.dom2.CDATASection
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.Node
import nl.adaptivity.xmlutil.dom2.Text
import nl.adaptivity.xmlutil.dom2.childNodes
import nl.adaptivity.xmlutil.dom2.data
import nl.adaptivity.xmlutil.dom2.firstChild
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.localName
import nl.adaptivity.xmlutil.dom2.namespaceURI
import nl.adaptivity.xmlutil.dom2.nextSibling
import nl.adaptivity.xmlutil.dom2.ownerDocument
import nl.adaptivity.xmlutil.dom2.parentNode

/**
 * One contiguous `<w:t>` text node inside a `<w:p>` together with
 * its position in the rendered string. The five replace passes
 * agree on this shape; only `ParagraphInjector` historically used
 * a stripped version (no textNode / text), but it never read those
 * fields — the cost of the extra references is one pointer per
 * span.
 */
internal data class TextSpan(
    val textNode: Text,
    val text: String,
    val start: Int,
    val endExclusive: Int,
)

/**
 * Concatenated text of a paragraph plus the index of every
 * contributing `<w:t>` node. Pure data; mutating callers operate
 * on the [TextSpan.textNode] references directly.
 */
internal data class RenderedParagraph(val text: String, val spans: List<TextSpan>)

/**
 * Walk a `<w:p>` element collecting every `<w:t>` text node into
 * a [RenderedParagraph]. Walks all descendants, so nested
 * `<w:hyperlink><w:r><w:t>...</w:t></w:r></w:hyperlink>` text is
 * captured under its parent paragraph.
 */
internal fun renderParagraph(paragraph: Element): RenderedParagraph {
    val spans = mutableListOf<TextSpan>()
    val builder = StringBuilder()
    collectTextSpans(paragraph, spans, builder)
    return RenderedParagraph(text = builder.toString(), spans = spans)
}

/**
 * Recursively walk [node] adding any `<w:t>` text node we encounter
 * to [spans] and appending its data to [builder].
 */
internal fun collectTextSpans(
    node: Node,
    spans: MutableList<TextSpan>,
    builder: StringBuilder,
) {
    if (node is Element &&
        node.namespaceURI == Namespaces.WORDPROCESSING_ML &&
        node.localName == "t"
    ) {
        val textNode = firstTextChild(node) ?: return
        val start = builder.length
        val text = textNode.data
        builder.append(text)
        spans += TextSpan(
            textNode = textNode,
            text = text,
            start = start,
            endExclusive = start + text.length,
        )
        return
    }
    val children = node.childNodes
    for (i in 0 until children.length) {
        collectTextSpans(children.item(i)!!, spans, builder)
    }
}

/** First TEXT_NODE child of [element], or `null`. */
internal fun firstTextChild(element: Element): Text? {
    val children = element.childNodes
    for (i in 0 until children.length) {
        val n = children.item(i) ?: continue
        // `is Text` matches both Text and its CDATASection
        // subtype; CDATASection is not a TEXT_NODE for our
        // purposes, so we filter it explicitly.
        if (n is Text && n !is CDATASection) return n
    }
    return null
}

/**
 * Climb [node]'s ancestor chain looking for a `<w:r>` element;
 * `null` if no ancestor in the wordprocessingML namespace matches.
 */
internal fun ancestorRun(node: Node): Element? {
    var current: Node? = node
    while (current != null) {
        if (current is Element &&
            current.namespaceURI == Namespaces.WORDPROCESSING_ML &&
            current.localName == "r"
        ) {
            return current
        }
        current = current.parentNode
    }
    return null
}

/**
 * DOM helper — insert [newNode] before [refNode] in [parent].
 *
 * `nl.adaptivity.xmlutil.dom2.Node` does not expose `insertBefore`
 * (DOM Level 1 includes it but the dom2 facade trims to the calls
 * the xmlutil core actually uses), so we splice manually:
 * detach `refNode` and every following sibling, append `newNode`,
 * then re-append the saved tail in order.
 */
internal fun insertBefore(parent: Node, newNode: Node, refNode: Node) {
    val tail = mutableListOf<Node>()
    var cur: Node? = refNode
    while (cur != null) {
        tail += cur
        cur = cur.nextSibling
    }
    for (n in tail) parent.removeChild(n)
    parent.appendChild(newNode)
    for (n in tail) parent.appendChild(n)
}

/**
 * DOM helper — insert [newNode] after [refNode] in [parent]. If
 * [refNode] is the last child, this degenerates to `appendChild`.
 */
internal fun insertAfter(parent: Node, newNode: Node, refNode: Node) {
    val next = refNode.nextSibling
    if (next == null) parent.appendChild(newNode)
    else insertBefore(parent, newNode, next)
}

/**
 * Deep clone [node] into a fresh detached copy owned by
 * [node]'s document. xmlutil's dom2 facade has no `cloneNode`,
 * but `Document.importNode(node, deep=true)` returns a fresh
 * detached copy with the same shape — used as a clone within
 * the same document.
 */
internal fun cloneDeep(node: Node): Node {
    return node.ownerDocument.importNode(node, /* deep = */ true)
}

/**
 * Build a fresh `<w:r>` cloning [sourceRun]'s `<w:rPr>` (if any)
 * and containing a single `<w:t xml:space="preserve">[text]</w:t>`.
 *
 * Used by all replace passes that split a marker run into multiple
 * pieces while preserving formatting — every cloned piece carries
 * the source run's properties.
 */
internal fun cloneRunWithText(sourceRun: Element, text: String): Element {
    val doc = sourceRun.ownerDocument
    val newRun = doc.createElementNS(Namespaces.WORDPROCESSING_ML, "w:r")
    // Copy the source's <w:rPr> first child if present.
    val children = sourceRun.childNodes
    for (i in 0 until children.length) {
        val n = children.item(i) ?: continue
        if (n is Element &&
            n.namespaceURI == Namespaces.WORDPROCESSING_ML &&
            n.localName == "rPr"
        ) {
            newRun.appendChild(cloneDeep(n))
            break
        }
    }
    val tEl = doc.createElementNS(Namespaces.WORDPROCESSING_ML, "w:t")
    tEl.setAttributeNS(Namespaces.XML_W3C, "xml:space", "preserve")
    tEl.appendChild(doc.createTextNode(text))
    newRun.appendChild(tEl)
    return newRun
}

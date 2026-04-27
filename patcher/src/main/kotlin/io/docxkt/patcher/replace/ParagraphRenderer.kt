// No upstream analogue — shared paragraph-rendering helpers used by
// TokenReplacer, ParagraphInjector, ImageInjector, RowInjector, and
// ParagraphInlineReplacer.
package io.docxkt.patcher.replace

import io.docxkt.xml.Namespaces
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text

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
    if (node.nodeType == Node.ELEMENT_NODE &&
        node.namespaceURI == Namespaces.WORDPROCESSING_ML &&
        node.localName == "t"
    ) {
        val element = node as Element
        val textNode = firstTextChild(element) ?: return
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
        collectTextSpans(children.item(i), spans, builder)
    }
}

/** First TEXT_NODE child of [element], or `null`. */
internal fun firstTextChild(element: Element): Text? {
    val children = element.childNodes
    for (i in 0 until children.length) {
        val n = children.item(i)
        if (n.nodeType == Node.TEXT_NODE) return n as Text
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
        if (current.nodeType == Node.ELEMENT_NODE &&
            current.namespaceURI == Namespaces.WORDPROCESSING_ML &&
            current.localName == "r"
        ) {
            return current as Element
        }
        current = current.parentNode
    }
    return null
}

/**
 * DOM helper — insert [newNode] after [refNode] in [parent]. If
 * [refNode] is the last child, this degenerates to `appendChild`.
 */
internal fun insertAfter(parent: Node, newNode: Node, refNode: Node) {
    val next = refNode.nextSibling
    if (next == null) parent.appendChild(newNode)
    else parent.insertBefore(newNode, next)
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
        val n = children.item(i)
        if (n.nodeType == Node.ELEMENT_NODE &&
            n.namespaceURI == Namespaces.WORDPROCESSING_ML &&
            n.localName == "rPr"
        ) {
            newRun.appendChild(n.cloneNode(true))
            break
        }
    }
    val tEl = doc.createElementNS(Namespaces.WORDPROCESSING_ML, "w:t")
    tEl.setAttributeNS(Namespaces.XML_W3C, "xml:space", "preserve")
    tEl.appendChild(doc.createTextNode(text))
    newRun.appendChild(tEl)
    return newRun
}

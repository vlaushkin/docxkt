// Port of: src/patcher/replacer.ts (PARAGRAPH branch with
// pre-built children) + src/patcher/paragraph-token-replacer.ts.
package io.docxkt.patcher.replace

import io.docxkt.patcher.Patch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.Text
import javax.xml.parsers.DocumentBuilderFactory

/**
 * INLINE replacement at a `{{key}}` marker. Splits the marker run
 * into prefix + (each inserted run) + suffix, then optionally
 * prepends the source run's `<w:rPr>` to each inserted run when
 * [PatchOptions.keepOriginalStyles] is true.
 *
 * Mirrors upstream's `PatchType.PARAGRAPH` branch in
 * `src/patcher/replacer.ts`.
 */
internal object ParagraphInlineReplacer {

    private const val W_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_ML
    private const val R_NAMESPACE = io.docxkt.xml.Namespaces.RELATIONSHIPS_OFFICE_DOCUMENT
    private const val WP_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_DRAWING
    private const val A_NAMESPACE = io.docxkt.xml.Namespaces.DRAWINGML_MAIN
    private const val PIC_NAMESPACE = io.docxkt.xml.Namespaces.DRAWINGML_PICTURE
    private const val MAX_ITERATIONS = 1000

    fun replace(
        doc: Document,
        patches: Map<String, Patch.ParagraphInline>,
        options: PatchOptions = PatchOptions(),
    ): Int {
        if (patches.isEmpty()) return 0
        val markerRegex = options.buildMarkerRegex()
        val active = patches.toMutableMap()
        var total = 0
        var iter = 0
        while (iter < MAX_ITERATIONS) {
            if (active.isEmpty()) return total
            val key = replaceOne(doc, active, markerRegex, options.keepOriginalStyles)
                ?: return total
            total += 1
            if (!options.recursive) active.remove(key)
            iter += 1
        }
        error("Inline replacement exceeded $MAX_ITERATIONS iterations — likely a self-referential patch")
    }

    private fun replaceOne(
        doc: Document,
        patches: Map<String, Patch.ParagraphInline>,
        markerRegex: Regex,
        keepOriginalStyles: Boolean,
    ): String? {
        val paragraphs = doc.getElementsByTagNameNS(W_NAMESPACE, "p")
        val paragraphList = (0 until paragraphs.length).map { paragraphs.item(it) as Element }
        for (paragraph in paragraphList) {
            val rendered = renderParagraph(paragraph)
            if (rendered.spans.isEmpty()) continue
            val match = markerRegex.find(rendered.text) ?: continue
            val key = match.groupValues[1]
            val patch = patches[key] ?: continue
            applyInline(rendered, match.range.first, match.range.last, patch, keepOriginalStyles)
            return key
        }
        return null
    }

    private fun applyInline(
        rendered: RenderedParagraph,
        markerStart: Int,
        markerEnd: Int,
        patch: Patch.ParagraphInline,
        keepOriginalStyles: Boolean,
    ) {
        // Locate first / last spans contributing to the marker.
        val firstIdx = rendered.spans.indexOfFirst {
            it.start <= markerStart && markerStart < it.endExclusive
        }
        val lastIdx = rendered.spans.indexOfFirst {
            it.start <= markerEnd && markerEnd < it.endExclusive
        }
        val first = rendered.spans[firstIdx]
        val last = rendered.spans[lastIdx]
        val firstRun = ancestorRun(first.textNode) ?: error("marker text has no <w:r> ancestor")
        val parent = firstRun.parentNode ?: error("<w:r> has no parent")
        val doc = firstRun.ownerDocument

        val firstLocalStart = markerStart - first.start
        val prefix = first.text.substring(0, firstLocalStart)
        val lastLocalEnd = markerEnd - last.start
        val suffix = last.text.substring(lastLocalEnd + 1)

        // Mirror upstream's two-step replace:
        //  1. paragraph-token-replacer rewrites text across spans:
        //     first marker span -> prefix, intermediates -> "" (empty
        //     w:t with NO text node, i.e. self-closing), last span ->
        //     suffix with xml:space="preserve" applied.
        //  2. splitRunElement on the first marker run produces
        //     LEFT (rPr + t-prefix) + RIGHT (t-empty); both with
        //     xml:space. Patched runs (with sourceRpr prepended)
        //     splice between them.
        first.textNode.data = prefix
        applyXmlSpacePreserve(first.textNode)
        for (i in (firstIdx + 1) until lastIdx) {
            // Intermediate marker spans: clear text AND remove the
            // text node child so the resulting <w:t/> self-closes
            // (matches upstream's createTextElementContents("")
            // returning an empty children array). Also strip any
            // xml:space attribute that a previous patch's
            // post-marker propagation may have added — upstream's
            // createTextElementContents("") returns a fresh
            // attribute-free text element.
            val tn = rendered.spans[i].textNode
            val parent = tn.parentNode
            if (parent != null && parent.nodeType == Node.ELEMENT_NODE) {
                (parent as Element).removeAttributeNS(
                    io.docxkt.xml.Namespaces.XML_W3C,
                    "space",
                )
            }
            parent?.removeChild(tn)
        }
        if (firstIdx != lastIdx) {
            last.textNode.data = suffix
            applyXmlSpacePreserve(last.textNode)
        }

        // Build inserted runs from snippets. Each snippet's <w:r> XML
        // is parsed via a minimal w:document envelope.
        val insertedRuns = parseRuns(doc, patch.snippets.toXml())
        // Optionally inherit the source run's <w:rPr> on each inserted run.
        if (keepOriginalStyles) {
            val sourceRpr = findRpr(firstRun)
            if (sourceRpr != null) {
                for (run in insertedRuns) prependRpr(run, sourceRpr)
            }
        }

        // Splice: [first run truncated to prefix] [patched] [right
        // half of first run]. Multi-span keeps intermediate + last
        // marker runs in their original positions; single-span uses
        // the suffix as the right-half text.
        var ref: Node = firstRun
        for (run in insertedRuns) {
            insertAfter(parent, run, ref)
            ref = run
        }
        val rightHalfText = if (firstIdx == lastIdx) suffix else ""
        val rightHalfRun = cloneRunWithSourceAttrs(firstRun, rightHalfText)
        insertAfter(parent, rightHalfRun, ref)

        // Upstream propagates xml:space="preserve" to the
        // run sitting immediately after the rebuilt marker. In
        // single-span the anchor is the rightHalfRun; in multi-span
        // it's the LAST marker run (whose textNode already received
        // xml:space above).
        val anchor: Node = if (firstIdx == lastIdx) rightHalfRun
            else (ancestorRun(last.textNode) ?: rightHalfRun)
        val afterMarker = runAfterMarkerText(anchor)
        if (afterMarker != null) applyXmlSpacePreserve(afterMarker)
    }

    private fun runAfterMarkerText(lastInsertedRun: Node): Text? {
        var n: Node? = lastInsertedRun.nextSibling
        while (n != null) {
            if (n.nodeType == Node.ELEMENT_NODE && n.namespaceURI == W_NAMESPACE && n.localName == "r") {
                val children = n.childNodes
                for (i in 0 until children.length) {
                    val c = children.item(i)
                    if (c.nodeType == Node.ELEMENT_NODE && c.namespaceURI == W_NAMESPACE && c.localName == "t") {
                        val tEl = c as Element
                        // Find the first text node child.
                        val tChildren = tEl.childNodes
                        for (j in 0 until tChildren.length) {
                            val tc = tChildren.item(j)
                            if (tc.nodeType == Node.TEXT_NODE) return tc as Text
                        }
                        return null
                    }
                }
                return null
            }
            n = n.nextSibling
        }
        return null
    }

    private fun applyXmlSpacePreserve(textNode: Text) {
        val parent = textNode.parentNode
        if (parent != null && parent.nodeType == Node.ELEMENT_NODE) {
            (parent as Element).setAttributeNS(
                io.docxkt.xml.Namespaces.XML_W3C,
                "xml:space",
                "preserve",
            )
        }
    }

    /**
     * Parse each `<w:r>...</w:r>` (or `<w:hyperlink>...</w:hyperlink>`)
     * snippet into a fresh DOM element rooted in [doc]. The wrapper
     * declares both `xmlns:w` and `xmlns:r` so hyperlink attributes
     * (`r:id="rIdN"`) bind correctly.
     */
    private fun parseRuns(doc: Document, runXmls: List<String>): List<Element> {
        val results = mutableListOf<Element>()
        val factory = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
        val builder = factory.newDocumentBuilder()
        for (xml in runXmls) {
            val wrapped = """<root xmlns:w="$W_NAMESPACE" xmlns:r="$R_NAMESPACE" xmlns:wp="$WP_NAMESPACE" xmlns:a="$A_NAMESPACE" xmlns:pic="$PIC_NAMESPACE">$xml</root>"""
            val parsed = builder.parse(wrapped.byteInputStream())
            val children = parsed.documentElement.childNodes
            for (i in 0 until children.length) {
                val n = children.item(i)
                if (n.nodeType == Node.ELEMENT_NODE && n.namespaceURI == W_NAMESPACE) {
                    if (n.localName == "r" || n.localName == "hyperlink") {
                        results += doc.importNode(n, true) as Element
                    }
                }
            }
        }
        return results
    }

    private fun findRpr(run: Element): Element? {
        val children = run.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i)
            if (n.nodeType == Node.ELEMENT_NODE && n.namespaceURI == W_NAMESPACE && n.localName == "rPr") {
                return n as Element
            }
        }
        return null
    }

    private fun prependRpr(run: Element, sourceRpr: Element) {
        // Match upstream's keepOriginalStyles=true:
        // always prepend the SOURCE rPr as a separate sibling rPr,
        // never merge into an existing one. This produces the
        // run-with-two-rPrs shape upstream emits in demos like 89:
        //   <w:r><w:rPr>…src…</w:rPr><w:rPr>…patch…</w:rPr><w:t>…</w:t></w:r>
        val cloned = run.ownerDocument.importNode(sourceRpr, true)
        val firstChild = run.firstChild
        if (firstChild != null) run.insertBefore(cloned, firstChild) else run.appendChild(cloned)
    }

    /**
     * ParagraphInlineReplacer-specific clone — preserves source-run
     * attributes (e.g. `w:rsidR`) on the suffix run. The shared
     * [cloneRunWithText] in [ParagraphRenderer] only copies `<w:rPr>`,
     * not run-level attributes.
     */
    private fun cloneRunWithSourceAttrs(sourceRun: Element, text: String): Element {
        val doc = sourceRun.ownerDocument
        val newRun = doc.createElementNS(W_NAMESPACE, "w:r")
        val attrs = sourceRun.attributes
        for (i in 0 until attrs.length) {
            val a = attrs.item(i) as org.w3c.dom.Attr
            newRun.setAttributeNS(a.namespaceURI, a.name, a.value)
        }
        val rPr = findRpr(sourceRun)
        if (rPr != null) newRun.appendChild(rPr.cloneNode(true))
        val tEl = doc.createElementNS(W_NAMESPACE, "w:t")
        tEl.setAttributeNS(io.docxkt.xml.Namespaces.XML_W3C, "xml:space", "preserve")
        tEl.appendChild(doc.createTextNode(text))
        newRun.appendChild(tEl)
        return newRun
    }
}

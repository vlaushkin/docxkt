// Port of: src/patcher/replacer.ts (DOCUMENT branch — splice the
// marker paragraph). Generalises upstream's whole-paragraph splice
// with an additional mid-paragraph split mode.
package io.docxkt.patcher.replace

import io.docxkt.patcher.Patch
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.Node
import nl.adaptivity.xmlutil.dom2.childNodes
import nl.adaptivity.xmlutil.dom2.data
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.localName
import nl.adaptivity.xmlutil.dom2.namespaceURI
import nl.adaptivity.xmlutil.dom2.parentNode

/**
 * Paragraph-injection pass over a parsed `word/document.xml` DOM.
 *
 * Walks every `<w:p>`, finds `{{key}}` markers that match a
 * registered [Patch.Paragraphs] entry, and replaces:
 *
 * - **Whole-paragraph replace** — when the marker is the
 *   paragraph's only visible text. The `<w:p>` is removed; the
 *   patch's snippet paragraphs are inserted in its place.
 * - **Mid-paragraph split** — when other text exists alongside
 *   the marker. The paragraph is split into a "before" half and
 *   "after" half (cloned from the source paragraph, with text
 *   in each clone trimmed to the relevant range and runs that
 *   fall entirely outside the range removed). The patch's
 *   snippets are inserted between the two halves.
 *
 * This pass runs AFTER [TokenReplacer] in the orchestrator so
 * that any `Patch.Text` markers are already substituted by the
 * time we look for `Patch.Paragraphs` markers.
 */
internal object ParagraphInjector {

    private const val W_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_ML

    /**
     * Mutate [doc] in-place. Returns the number of paragraph
     * patches applied.
     */
    fun inject(
        doc: Document,
        patches: Map<String, Patch.Paragraphs>,
        options: PatchOptions = PatchOptions(),
    ): Int {
        if (patches.isEmpty()) return 0
        val markerRegex = options.buildMarkerRegex()
        val active = patches.toMutableMap()
        var totalApplied = 0
        // Re-walk after each application: splice changes the
        // body's child list, so we can't iterate live.
        while (active.isNotEmpty()) {
            val appliedKey = injectOne(doc, active, markerRegex) ?: return totalApplied
            totalApplied += 1
            if (!options.recursive) active.remove(appliedKey)
        }
        return totalApplied
    }

    private fun injectOne(doc: Document, patches: Map<String, Patch.Paragraphs>, markerRegex: Regex): String? {
        val root = doc.documentElement!!
        val paragraphs = root.getElementsByTagNameNS(W_NAMESPACE, "p")
        val paragraphList = (0 until paragraphs.length).map { paragraphs.item(it) as Element }

        for (paragraph in paragraphList) {
            val key = tryInjectInto(paragraph, doc, patches, markerRegex)
            if (key != null) return key
        }
        return null
    }

    /**
     * Try to apply a paragraph patch to [paragraph]. Returns the
     * matched key on success (caller should restart walking, since
     * the document tree changed), or `null` if no match.
     */
    private fun tryInjectInto(
        paragraph: Element,
        doc: Document,
        patches: Map<String, Patch.Paragraphs>,
        markerRegex: Regex,
    ): String? {
        val rendered = renderParagraph(paragraph)
        if (rendered.spans.isEmpty()) return null
        val match = markerRegex.find(rendered.text) ?: return null
        val key = match.groupValues[1]
        val patch = patches[key] ?: return null

        val markerStart = match.range.first
        val markerEnd = match.range.last
        val isWholeParagraph = isMarkerOnlyText(rendered, markerStart, markerEnd)

        val parent = paragraph.parentNode ?: error("Paragraph has no parent")
        // Snippets may be paragraphs OR tables.
        val newParagraphs = patch.snippets.toXml().map {
            ParagraphSnippetParser.parseAndImportBlock(it, doc)
        }

        if (isWholeParagraph) {
            // Splice <w:p> out, insert new paragraphs before its old position.
            for (np in newParagraphs) {
                insertBefore(parent, np, paragraph)
            }
            parent.removeChild(paragraph)
        } else {
            // Mid-paragraph split.
            val (before, after) = splitParagraph(paragraph, doc, rendered, markerStart, markerEnd)
            // Insert: before, ...newParagraphs, after; then remove
            // the original.
            insertBefore(parent, before, paragraph)
            for (np in newParagraphs) insertBefore(parent, np, paragraph)
            insertBefore(parent, after, paragraph)
            parent.removeChild(paragraph)
        }
        return key
    }

    /**
     * `true` iff the marker spans the entire visible text of the
     * paragraph (i.e. the paragraph contains no other text).
     */
    private fun isMarkerOnlyText(
        rendered: RenderedParagraph,
        markerStart: Int,
        markerEnd: Int,
    ): Boolean {
        val before = rendered.text.substring(0, markerStart)
        val after = rendered.text.substring(markerEnd + 1)
        return before.isBlank() && after.isBlank()
    }

    /**
     * Split [paragraph] at the marker into a "before" clone and
     * an "after" clone. Each clone has the marker AND irrelevant
     * runs removed, leaving only the prefix or suffix text.
     *
     * Strategy: clone twice. For each clone, walk its `<w:t>`
     * descendants, mutating their text data to keep only the
     * relevant slice. `<w:r>` elements that contributed no text
     * to the relevant slice are removed.
     */
    private fun splitParagraph(
        paragraph: Element,
        doc: Document,
        rendered: RenderedParagraph,
        markerStart: Int,
        markerEnd: Int,
    ): Pair<Element, Element> {
        // BEFORE half: everything up to markerStart.
        val before = cloneDeep(paragraph) as Element
        truncateParagraph(before, /* keepStart = */ 0, /* keepEndExclusive = */ markerStart)
        // AFTER half: everything after markerEnd.
        val after = cloneDeep(paragraph) as Element
        truncateParagraph(after, /* keepStart = */ markerEnd + 1, /* keepEndExclusive = */ rendered.text.length)
        return before to after
    }

    /**
     * Walk the cloned [paragraph]'s `<w:t>` text nodes in document
     * order, with running global offsets. For each:
     *
     * - If the text falls entirely outside `[keepStart,
     *   keepEndExclusive)`, blank its content and mark its parent
     *   `<w:r>` for removal (if all its `<w:t>`s are blanked).
     * - If it falls partially in, trim to the in-range slice.
     * - If it falls entirely in, leave alone.
     */
    private fun truncateParagraph(
        paragraph: Element,
        keepStart: Int,
        keepEndExclusive: Int,
    ) {
        val tNodes = mutableListOf<Element>()
        collectTextElements(paragraph, tNodes)
        var globalOffset = 0
        for (t in tNodes) {
            val textChild = firstTextChild(t) ?: continue
            val text = textChild.data
            val nodeStart = globalOffset
            val nodeEndExclusive = nodeStart + text.length
            globalOffset = nodeEndExclusive

            val sliceStart = maxOf(nodeStart, keepStart)
            val sliceEndExclusive = minOf(nodeEndExclusive, keepEndExclusive)
            if (sliceStart >= sliceEndExclusive) {
                // Entirely outside the keep range.
                textChild.data = ""
            } else if (sliceStart != nodeStart || sliceEndExclusive != nodeEndExclusive) {
                // Partial overlap — trim.
                val localStart = sliceStart - nodeStart
                val localEnd = sliceEndExclusive - nodeStart
                textChild.data = text.substring(localStart, localEnd)
            }
            // Else: fully in range; leave.
        }

        // Now remove <w:r> elements where every contained <w:t>
        // is empty (no text data). Defensive: also remove <w:r>
        // elements containing zero <w:t>s? Probably not — they
        // might carry other inline content. Conservative: only
        // remove if the run contained <w:t>s and all are now
        // empty.
        val toRemove = mutableListOf<Element>()
        val runs = paragraph.getElementsByTagNameNS(W_NAMESPACE, "r")
        for (i in 0 until runs.length) {
            val r = runs.item(i) as Element
            val tChildren = childElementsNS(r, W_NAMESPACE, "t")
            if (tChildren.isNotEmpty() && tChildren.all { (firstTextChild(it)?.data ?: "").isEmpty() }) {
                toRemove += r
            }
        }
        for (r in toRemove) {
            r.parentNode?.removeChild(r)
        }
    }

    private fun collectTextElements(node: Node, out: MutableList<Element>) {
        if (node is Element && node.namespaceURI == W_NAMESPACE && node.localName == "t") {
            out += node
            return
        }
        val children = node.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i) ?: continue
            collectTextElements(n, out)
        }
    }

    private fun childElementsNS(parent: Element, ns: String, local: String): List<Element> {
        val result = mutableListOf<Element>()
        val children = parent.childNodes
        for (i in 0 until children.length) {
            val n = children.item(i) ?: continue
            if (n is Element && n.namespaceURI == ns && n.localName == local) {
                result += n
            }
        }
        return result
    }

}

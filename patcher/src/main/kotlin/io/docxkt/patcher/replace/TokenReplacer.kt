// Port of: src/patcher/replacer.ts (PARAGRAPH branch) +
//          src/patcher/paragraph-token-replacer.ts +
//          src/patcher/run-renderer.ts.
package io.docxkt.patcher.replace

import io.docxkt.patcher.Patch
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.Element
import nl.adaptivity.xmlutil.dom2.data
import nl.adaptivity.xmlutil.dom2.documentElement
import nl.adaptivity.xmlutil.dom2.length
import nl.adaptivity.xmlutil.dom2.ownerDocument
import nl.adaptivity.xmlutil.dom2.parentNode

/**
 * Token-replacement pass over a parsed `word/document.xml` DOM.
 *
 * Walks every `<w:p>` paragraph, concatenates the text content of
 * its `<w:t>` children, and replaces marker substrings (delimited
 * by [PatchOptions.placeholderDelimiters], default `{{`/`}}`) with
 * the value supplied by the matching [Patch.Text] entry.
 *
 * **Spanning runs:** when a marker straddles run boundaries, the
 * replacement text lands inside the FIRST contributing run (so
 * inheriting that run's `<w:rPr>` formatting), provided
 * [PatchOptions.keepOriginalStyles] is true. With
 * `keepOriginalStyles = false`, the replacement goes into a fresh
 * bare `<w:r>` (no `<w:rPr>`) inserted between split halves of
 * the source run.
 *
 * **Recursion:** with [PatchOptions.recursive] = true (default),
 * we re-scan after each replacement until no more markers match
 * (handles values that themselves contain markers). With
 * `recursive = false`, each key is replaced at most once per
 * `patch()` call. Capped at 1000 iterations on the recursive
 * path to avoid infinite loops on adversarial input.
 */
internal object TokenReplacer {

    private const val W_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_ML
    private const val MAX_ITERATIONS = 1000

    /**
     * Mutate [doc] in-place: replace marker substrings with
     * `patches[key].value` for entries that are [Patch.Text].
     * Returns the number of replacements made.
     */
    fun replace(
        doc: Document,
        patches: Map<String, Patch.Text>,
        options: PatchOptions = PatchOptions(),
    ): Int {
        if (patches.isEmpty()) return 0

        val markerRegex = options.buildMarkerRegex()
        // Mutable map so we can drop keys that have been replaced
        // once when recursive=false.
        val active = patches.toMutableMap()
        var totalReplacements = 0
        var iteration = 0
        while (iteration < MAX_ITERATIONS) {
            if (active.isEmpty()) return totalReplacements
            val replacedKey = replaceOne(doc, active, markerRegex, options.keepOriginalStyles)
                ?: return totalReplacements
            totalReplacements += 1
            if (!options.recursive) active.remove(replacedKey)
            iteration += 1
        }
        error("Token replacement exceeded $MAX_ITERATIONS iterations — likely a self-referential patch loop")
    }

    /**
     * Walk paragraphs, find the first marker matching a registered
     * patch, replace it. Returns the key replaced (or null if no
     * matching marker found).
     */
    private fun replaceOne(
        doc: Document,
        patches: Map<String, Patch.Text>,
        markerRegex: Regex,
        keepOriginalStyles: Boolean,
    ): String? {
        val root = doc.documentElement!!
        val paragraphs = root.getElementsByTagNameNS(W_NAMESPACE, "p")
        val paragraphList = (0 until paragraphs.length).map { paragraphs.item(it) as Element }

        for (paragraph in paragraphList) {
            val rendered = renderParagraph(paragraph)
            if (rendered.spans.isEmpty()) continue
            val match = markerRegex.find(rendered.text) ?: continue
            val key = match.groupValues[1]
            val patch = patches[key] ?: continue
            applyReplacement(
                rendered = rendered,
                markerStart = match.range.first,
                markerEnd = match.range.last,
                replacement = patch.value,
                keepOriginalStyles = keepOriginalStyles,
            )
            return key
        }
        return null
    }

    /**
     * Replace the character range `[markerStart..markerEnd]`
     * (inclusive) in [rendered] with [replacement]. Mutates the
     * DOM `Text` nodes (and possibly inserts new `<w:r>` siblings)
     * referenced by [rendered.spans].
     */
    private fun applyReplacement(
        rendered: RenderedParagraph,
        markerStart: Int,
        markerEnd: Int,
        replacement: String,
        keepOriginalStyles: Boolean,
    ) {
        val firstIdx = rendered.spans.indexOfFirst { it.endExclusive > markerStart }
        val lastIdx = rendered.spans.indexOfLast { it.start <= markerEnd }
        check(firstIdx >= 0 && lastIdx >= firstIdx)

        val first = rendered.spans[firstIdx]
        val last = rendered.spans[lastIdx]

        if (keepOriginalStyles) {
            applyKeepingStyles(rendered, firstIdx, lastIdx, first, last, markerStart, markerEnd, replacement)
        } else {
            applyStrippingStyles(rendered, firstIdx, lastIdx, first, last, markerStart, markerEnd, replacement)
        }
    }

    /**
     * `keepOriginalStyles=true` — replacement inherits the first
     * contributing run's `<w:rPr>` by going into its `<w:t>`.
     */
    private fun applyKeepingStyles(
        rendered: RenderedParagraph,
        firstIdx: Int,
        lastIdx: Int,
        first: TextSpan,
        last: TextSpan,
        markerStart: Int,
        markerEnd: Int,
        replacement: String,
    ) {
        if (firstIdx == lastIdx) {
            val localStart = markerStart - first.start
            val localEnd = markerEnd - first.start
            val before = first.text.substring(0, localStart)
            val after = first.text.substring(localEnd + 1)
            first.textNode.data = before + replacement + after
        } else {
            val firstLocalStart = markerStart - first.start
            first.textNode.data = first.text.substring(0, firstLocalStart) + replacement
            for (i in (firstIdx + 1) until lastIdx) {
                rendered.spans[i].textNode.data = ""
            }
            val lastLocalEnd = markerEnd - last.start
            last.textNode.data = last.text.substring(lastLocalEnd + 1)
        }
    }

    /**
     * `keepOriginalStyles=false` — replacement lands in a fresh
     * bare `<w:r>` (no `<w:rPr>`) inserted between the prefix and
     * suffix halves of the marker's source run.
     *
     * Single-span case: the source run is structurally split into
     * three runs (prefix-clone, bare replacement, suffix-clone).
     * Multi-span case: the first run keeps the prefix, the last run
     * keeps the suffix, intermediate runs are emptied, and a bare
     * replacement run is inserted between the first run and the
     * subsequent runs.
     */
    private fun applyStrippingStyles(
        rendered: RenderedParagraph,
        firstIdx: Int,
        lastIdx: Int,
        first: TextSpan,
        last: TextSpan,
        markerStart: Int,
        markerEnd: Int,
        replacement: String,
    ) {
        val firstRun = ancestorRun(first.textNode)
            ?: error("First marker text node has no <w:r> ancestor")
        val parent = firstRun.parentNode ?: error("<w:r> has no parent")
        val doc = firstRun.ownerDocument

        val firstLocalStart = markerStart - first.start
        val prefix = first.text.substring(0, firstLocalStart)
        val lastLocalEnd = markerEnd - last.start
        val suffix = last.text.substring(lastLocalEnd + 1)

        if (firstIdx == lastIdx) {
            // Single span: split the run.
            // 1. firstRun's <w:t> → prefix
            first.textNode.data = prefix
            // 2. Insert bare run with replacement
            val bareRun = createBareTextRun(doc, replacement)
            insertAfter(parent, bareRun, firstRun)
            // 3. Insert clone-of-firstRun-with-suffix
            if (suffix.isNotEmpty()) {
                val suffixRun = cloneRunWithText(firstRun, suffix)
                insertAfter(parent, suffixRun, bareRun)
            }
        } else {
            // Multi span: trim first and last, blank intermediates,
            // inject bare run after firstRun.
            first.textNode.data = prefix
            for (i in (firstIdx + 1) until lastIdx) {
                rendered.spans[i].textNode.data = ""
            }
            last.textNode.data = suffix
            val bareRun = createBareTextRun(doc, replacement)
            insertAfter(parent, bareRun, firstRun)
        }
    }

    private fun createBareTextRun(doc: Document, text: String): Element {
        val r = doc.createElementNS(W_NAMESPACE, "w:r")
        val t = doc.createElementNS(W_NAMESPACE, "w:t")
        t.setAttributeNS(io.docxkt.xml.Namespaces.XML_W3C, "xml:space", "preserve")
        t.appendChild(doc.createTextNode(text))
        r.appendChild(t)
        return r
    }

}

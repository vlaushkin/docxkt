// Port of: src/patcher/replacer.ts (DOCUMENT branch — splice
// the marker's enclosing element). Adapted to splice <w:tr>
// rather than <w:p>.
package io.docxkt.patcher.replace

import io.docxkt.patcher.Patch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Row-injection pass. For each marker matching a registered
 * [Patch.Rows] entry, locate the marker's enclosing `<w:tr>`,
 * splice it out of its parent `<w:tbl>`, and insert the snippet
 * rows in its place.
 *
 * Whole-row replacement only — mid-row splits are not supported.
 * The marker can sit anywhere in any cell of the row; the entire
 * row is replaced.
 */
internal object RowInjector {

    private const val W_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_ML

    fun inject(
        doc: Document,
        patches: Map<String, Patch.Rows>,
        options: PatchOptions = PatchOptions(),
    ): Int {
        if (patches.isEmpty()) return 0
        val markerRegex = options.buildMarkerRegex()
        val active = patches.toMutableMap()
        var totalApplied = 0
        while (active.isNotEmpty()) {
            val appliedKey = applyOne(doc, active, markerRegex) ?: return totalApplied
            totalApplied += 1
            if (!options.recursive) active.remove(appliedKey)
        }
        return totalApplied
    }

    private fun applyOne(doc: Document, patches: Map<String, Patch.Rows>, markerRegex: Regex): String? {
        // Walk text nodes inside <w:t> via paragraph traversal.
        val paragraphs = doc.getElementsByTagNameNS(W_NAMESPACE, "p")
        for (i in 0 until paragraphs.length) {
            val p = paragraphs.item(i) as Element
            val containingRow = ancestorRow(p) ?: continue
            val text = renderParagraph(p).text
            val match = markerRegex.find(text) ?: continue
            val key = match.groupValues[1]
            val patch = patches[key] ?: continue

            val parent = containingRow.parentNode ?: error("<w:tr> has no parent")
            val newRows = patch.snippets.toXml().map {
                ParagraphSnippetParser.parseAndImportRow(it, doc)
            }
            for (newRow in newRows) {
                parent.insertBefore(newRow, containingRow)
            }
            parent.removeChild(containingRow)
            return key
        }
        return null
    }

    /** `<w:tr>` ancestor walk — analogous to [ancestorRun] but for rows. */
    private fun ancestorRow(node: Node): Element? {
        var current: Node? = node
        while (current != null) {
            if (current.nodeType == Node.ELEMENT_NODE &&
                current.namespaceURI == W_NAMESPACE &&
                current.localName == "tr"
            ) {
                return current as Element
            }
            current = current.parentNode
        }
        return null
    }
}

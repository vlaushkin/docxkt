// Port of: src/patcher/replacer.ts (PARAGRAPH branch with ImageRun
// child) + src/export/packer/image-replacer.ts.
package io.docxkt.patcher.replace

import io.docxkt.api.runs
import io.docxkt.patcher.Patch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.Node

/**
 * Image-injection pass.
 *
 * For each `Patch.Image` keyed by some name, finds the
 * corresponding `{{name}}` marker in `word/document.xml`, splits
 * the marker run into prefix + drawing + suffix runs, and registers
 * the side effects: a new image relationship in
 * `word/_rels/document.xml.rels`, a `<Default>` content-type entry
 * in `[Content_Types].xml`, and a media file (returned to the
 * orchestrator).
 *
 * The drawing wire shape is hardcoded as a string template — see
 * the [DRAWING_TEMPLATE] doc comment for the cross-reference to
 * `:core`'s Drawing.appendXml.
 */
internal object ImageInjector {

    private const val W_NAMESPACE = io.docxkt.xml.Namespaces.WORDPROCESSING_ML

    /**
     * One media binary the orchestrator must add to the output ZIP.
     */
    data class MediaEntry(val path: String, val bytes: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is MediaEntry) return false
            return path == other.path && bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = 31 * path.hashCode() + bytes.contentHashCode()
    }

    /**
     * Apply image patches. Mutates [documentDoc], [contentTypesDoc],
     * and [relsDoc] in place. Returns the list of new media files
     * the caller must write to the output ZIP.
     */
    fun inject(
        documentDoc: Document,
        contentTypesDoc: Document,
        relsDoc: Document,
        patches: Map<String, Patch.Image>,
        options: PatchOptions = PatchOptions(),
    ): List<MediaEntry> {
        if (patches.isEmpty()) return emptyList()

        val markerRegex = options.buildMarkerRegex()
        val active = patches.toMutableMap()
        val newMedia = mutableListOf<MediaEntry>()
        var nextRid = RelationshipManager.nextRid(relsDoc)
        var nextImageIdx = RelationshipManager.maxImageMediaIndex(relsDoc) + 1

        // Loop until all matching markers are consumed. Each
        // iteration walks for one marker, applies, restarts.
        while (active.isNotEmpty()) {
            val result = applyOne(documentDoc, active, markerRegex, nextRid, nextImageIdx) ?: break
            newMedia += MediaEntry(
                path = "word/media/image${nextImageIdx}.${result.patch.format.extension}",
                bytes = result.patch.bytes,
            )
            // Register side effects.
            ContentTypesManager.addDefaultExtension(
                contentTypesDoc,
                extension = result.patch.format.extension,
                contentType = result.patch.format.mimeType,
            )
            RelationshipManager.addRelationship(
                relsDoc,
                id = nextRid,
                type = RelationshipManager.IMAGE_TYPE,
                target = "media/image${nextImageIdx}.${result.patch.format.extension}",
            )
            nextRid += 1
            nextImageIdx += 1
            if (!options.recursive) active.remove(result.key)
        }
        return newMedia
    }

    private data class ApplyResult(val key: String, val patch: Patch.Image)

    /**
     * Walk paragraphs, find the first marker matching a registered
     * image patch, replace it. Returns the patch that was applied
     * (so the caller can record media), or `null` if no marker
     * matched.
     */
    private fun applyOne(
        doc: Document,
        patches: Map<String, Patch.Image>,
        markerRegex: Regex,
        rid: Int,
        imageIdx: Int,
    ): ApplyResult? {
        val paragraphs = doc.getElementsByTagNameNS(W_NAMESPACE, "p")
        val pList = (0 until paragraphs.length).map { paragraphs.item(it) as Element }
        for (paragraph in pList) {
            val rendered = renderParagraph(paragraph)
            val match = markerRegex.find(rendered.text) ?: continue
            val key = match.groupValues[1]
            val patch = patches[key] ?: continue
            replaceMarkerWithImage(
                paragraph = paragraph,
                rendered = rendered,
                markerStart = match.range.first,
                markerEnd = match.range.last,
                rid = rid,
                patch = patch,
            )
            return ApplyResult(key = key, patch = patch)
        }
        return null
    }

    /**
     * Replace the marker `[markerStart..markerEnd]` in [paragraph]
     * with three runs: prefix-text, drawing, suffix-text. Empty
     * prefix/suffix runs are omitted.
     */
    private fun replaceMarkerWithImage(
        paragraph: Element,
        rendered: RenderedParagraph,
        markerStart: Int,
        markerEnd: Int,
        rid: Int,
        patch: Patch.Image,
    ) {
        val firstIdx = rendered.spans.indexOfFirst { it.endExclusive > markerStart }
        val lastIdx = rendered.spans.indexOfLast { it.start <= markerEnd }
        val first = rendered.spans[firstIdx]
        val last = rendered.spans[lastIdx]

        // Determine prefix / suffix text and the source <w:r>
        // that contains the marker's first character (we'll
        // anchor our splice on it).
        val firstLocalStart = markerStart - first.start
        val prefix = first.text.substring(0, firstLocalStart)

        val lastLocalEnd = markerEnd - last.start
        val suffix = last.text.substring(lastLocalEnd + 1)

        val firstRun = ancestorRun(first.textNode)
            ?: error("First marker text node has no <w:r> ancestor")
        val parent = firstRun.parentNode

        // 1. Mutate the first <w:t>'s data to be just the prefix
        //    text. If prefix is empty, the run still works but
        //    contains an empty <w:t>. We could remove the first
        //    run entirely if prefix is empty AND the run had no
        //    other children, but for simplicity we keep the
        //    structure.
        first.textNode.data = prefix

        // 2. Build the drawing run XML through :core's public
        // `runs { image(…) }` snippet path and import it.
        val drawingRunXml = buildDrawingRunXml(
            rid = rid,
            widthEmus = patch.widthEmus,
            heightEmus = patch.heightEmus,
            bytes = patch.bytes,
            format = patch.format,
        )
        val drawingRun = ParagraphSnippetParser.parseAndImportRun(drawingRunXml, paragraph.ownerDocument)

        // 3. For multi-span markers, blank/trim the spans
        //    between first and last per the same rules as
        //    TokenReplacer (intermediate spans empty, last span
        //    becomes suffix).
        if (firstIdx == lastIdx) {
            // Marker fully inside one <w:t> — set first node's
            // text to prefix only; the suffix will go into a new
            // run inserted AFTER drawingRun.
            first.textNode.data = prefix
            // Insert drawingRun and suffix run after firstRun.
            insertAfter(parent, drawingRun, firstRun)
            if (suffix.isNotEmpty()) {
                val suffixRun = cloneRunWithText(firstRun, suffix)
                insertAfter(parent, suffixRun, drawingRun)
            }
        } else {
            // Multi-span: clear intermediate spans, keep first as
            // prefix, set last as suffix. Insert drawingRun between
            // firstRun and last's ancestor run. The suffix in last's
            // <w:t> stays inside its own <w:r>, which already comes
            // after drawingRun in document order — no extra suffix
            // run needed. Intermediate runs hold cleared <w:t>s but
            // remain in document order, which Word renders as
            // empty (acceptable per the contract).
            for (i in (firstIdx + 1) until lastIdx) {
                rendered.spans[i].textNode.data = ""
            }
            last.textNode.data = suffix
            insertAfter(parent, drawingRun, firstRun)
        }
    }

    /**
     * The inline drawing run XML for `Patch.Image`. Routes through
     * `:core`'s public [io.docxkt.api.runs] DSL so the wire shape
     * stays in lock-step with [Drawing.appendXml] automatically
     * (no hardcoded string template to hand-synchronise).
     *
     * `wp:docPr id` defaults to 1 inside [Drawing.appendXml] —
     * matching the patcher fixture expectation for first-image
     * insertions. Multi-image patches in a single call all emit
     * `wp:docPr id="1"`; this matches the pre-Phase-57c behaviour.
     */
    private fun buildDrawingRunXml(
        rid: Int,
        widthEmus: Int,
        heightEmus: Int,
        bytes: ByteArray,
        format: io.docxkt.model.drawing.ImageFormat,
        description: String? = null,
    ): String {
        val snippet = runs {
            image(
                bytes = bytes,
                widthEmus = widthEmus,
                heightEmus = heightEmus,
                format = format,
                description = description,
            )
        }
        val binding = snippet.imageBindings().single()
        snippet.resolveImage(binding, "rId$rid")
        return snippet.toXml().single()
    }

}

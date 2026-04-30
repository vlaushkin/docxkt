// Port of: src/patcher/from-docx.ts (`patchDocument` entry point).
package io.docxkt.patcher

import io.docxkt.pack.DocxPackager
import io.docxkt.pack.toByteArray
import io.docxkt.patcher.io.DocxReader
import io.docxkt.patcher.io.OoxmlParser
import io.docxkt.patcher.io.OoxmlWriter
import io.docxkt.patcher.replace.ImageInjector
import io.docxkt.patcher.replace.ParagraphInjector
import io.docxkt.patcher.replace.PatchOptions
import io.docxkt.patcher.replace.RowInjector
import io.docxkt.patcher.replace.TokenReplacer
import nl.adaptivity.xmlutil.dom2.Document
import nl.adaptivity.xmlutil.dom2.documentElement

/**
 * Top-level patcher entry point.
 *
 * Mirrors upstream's `patchDocument({ data, patches, … })` shape.
 * Supports five concrete patch types:
 *
 * - [Patch.Text] — replace marker with literal text.
 * - [Patch.Paragraphs] — replace/split paragraph at marker.
 * - [Patch.Image] — inline image embed at marker.
 * - [Patch.Rows] — table-row injection.
 * - [Patch.ParagraphInline] — inline replacement at marker
 *   preserving surrounding paragraph content.
 *
 * Round-trip semantics:
 *
 * - ZIP entries are read in source order.
 * - XML parts (`*.xml`, `*.rels`) are parsed to a DOM and serialized
 *   back. The byte sequence after round-trip is NOT guaranteed equal
 *   to the input — JAXP's serializer may emit empty elements as
 *   `<x></x>` instead of `<x/>` and may reorder attributes — but the
 *   *semantic* XML payload round-trips losslessly.
 * - Binary parts pass through verbatim. Image patches add new media
 *   entries to the output.
 *
 * Use XMLUnit at the test layer to assert round-trip equivalence on
 * compared parts; do not assert on raw byte equality.
 */
public object PatchDocument {

    /**
     * Patch [data] using [patches] and return the resulting `.docx`
     * bytes. With [patches] empty, the result is a re-zip of the
     * input parts.
     *
     * Options:
     * - [keepOriginalStyles] (default `true`) — replacement text
     *   inherits the source run's `<w:rPr>`. Set `false` to insert
     *   the replacement in a fresh bare `<w:r>`.
     * - [placeholderDelimiters] (default `"{{" to "}}"`) — custom
     *   marker delimiters. Both halves are `Regex.escape`d so any
     *   characters are legal.
     * - [recursive] (default `true`) — when a replacement value
     *   contains another `{{key}}` for a registered patch, the
     *   replacer scans again. Set `false` to replace each key at
     *   most once per `patch()` call.
     */
    public fun patch(
        data: ByteArray,
        patches: Map<String, Patch> = emptyMap(),
        keepOriginalStyles: Boolean = true,
        placeholderDelimiters: Pair<String, String> = "{{" to "}}",
        recursive: Boolean = true,
    ): ByteArray {
        // Match upstream `from-docx.ts:272-274`: empty delimiters
        // would expand Regex.escape("") + "(.+?)" + Regex.escape("")
        // to "(.+?)", which matches every non-empty character span
        // and rewrites the document into garbage. Surface as a clear
        // IllegalArgumentException.
        val (delimStart, delimEnd) = placeholderDelimiters
        require(delimStart.isNotEmpty() && delimEnd.isNotEmpty()) {
            "placeholderDelimiters must be non-empty on both sides; got (\"$delimStart\", \"$delimEnd\")"
        }
        val parts = DocxReader.read(data)
        val options = PatchOptions(
            keepOriginalStyles = keepOriginalStyles,
            placeholderStart = placeholderDelimiters.first,
            placeholderEnd = placeholderDelimiters.second,
            recursive = recursive,
        )

        // Bucket patches by concrete subtype for per-pass dispatch.
        val textPatches: Map<String, Patch.Text> = patches
            .mapNotNull { (k, v) -> if (v is Patch.Text) k to v else null }
            .toMap()
        val paragraphPatches: Map<String, Patch.Paragraphs> = patches
            .mapNotNull { (k, v) -> if (v is Patch.Paragraphs) k to v else null }
            .toMap()
        val imagePatches: Map<String, Patch.Image> = patches
            .mapNotNull { (k, v) -> if (v is Patch.Image) k to v else null }
            .toMap()
        val rowPatches: Map<String, Patch.Rows> = patches
            .mapNotNull { (k, v) -> if (v is Patch.Rows) k to v else null }
            .toMap()
        val inlinePatches: Map<String, Patch.ParagraphInline> = patches
            .mapNotNull { (k, v) -> if (v is Patch.ParagraphInline) k to v else null }
            .toMap()

        // Parse all writable XML parts up-front. Image patches need
        // to mutate document.xml + content_types + document.xml.rels
        // in concert.
        val parsedXml = mutableMapOf<String, Document>()
        for ((path, bytes) in parts) {
            if (isXmlPart(path)) {
                parsedXml[path] = OoxmlParser.parse(bytes)
            }
        }

        // Apply patches in order: Text → Paragraphs → Image → Rows.
        val documentDoc = parsedXml["word/document.xml"]
        if (documentDoc != null) {
            // Match upstream `from-docx.ts` post-load pass: ensure
            // mc/wp/r/w15/m namespace declarations are set on
            // <w:document> (in case patches introduce elements in
            // those namespaces) and append " w15" to mc:Ignorable.
            ensureDocumentNamespaces(documentDoc)

            if (textPatches.isNotEmpty()) {
                TokenReplacer.replace(documentDoc, textPatches, options)
            }
        }

        // Media accumulator — both Patch.Image and the
        // hyperlink/image-in-snippet flow append to this. Flushed
        // to the output ZIP at the end.
        val newMedia = mutableListOf<ImageInjector.MediaEntry>()

        // Paragraph patches may carry snippets with hyperlinks and
        // images; resolve their slots before ParagraphInjector
        // formats them.
        if (paragraphPatches.isNotEmpty() && documentDoc != null) {
            val needsRels = paragraphPatches.values.any {
                it.snippets.hyperlinkBindings().isNotEmpty() ||
                    it.snippets.imageBindings().isNotEmpty()
            }
            if (needsRels) {
                val relsDoc = parsedXml.getOrPut("word/_rels/document.xml.rels") {
                    synthesizeEmptyRelsDocument()
                }
                var nextRid = io.docxkt.patcher.replace.RelationshipManager.nextRid(relsDoc)
                var nextImageIdx = io.docxkt.patcher.replace.RelationshipManager
                    .maxImageMediaIndex(relsDoc) + 1
                val seenExt = mutableSetOf<String>()
                for ((_, patch) in paragraphPatches) {
                    for (binding in patch.snippets.hyperlinkBindings()) {
                        val rid = "rId$nextRid"
                        io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                            doc = relsDoc,
                            id = nextRid,
                            type = io.docxkt.patcher.replace.RelationshipManager.HYPERLINK_TYPE,
                            target = binding.target,
                            targetMode = "External",
                        )
                        patch.snippets.resolveHyperlink(binding, rid)
                        nextRid += 1
                    }
                    for (binding in patch.snippets.imageBindings()) {
                        val rid = "rId$nextRid"
                        val mediaPath = "media/image${nextImageIdx}.${binding.format.extension}"
                        io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                            doc = relsDoc,
                            id = nextRid,
                            type = io.docxkt.patcher.replace.RelationshipManager.IMAGE_TYPE,
                            target = mediaPath,
                        )
                        patch.snippets.resolveImage(binding, rid)
                        newMedia += ImageInjector.MediaEntry(
                            path = "word/$mediaPath",
                            bytes = binding.bytes,
                        )
                        if (seenExt.add(binding.format.extension)) {
                            val ctDoc = parsedXml["[Content_Types].xml"]
                            if (ctDoc != null) {
                                io.docxkt.patcher.replace.ContentTypesManager.addDefaultExtension(
                                    doc = ctDoc,
                                    extension = binding.format.extension,
                                    contentType = binding.format.mimeType,
                                )
                            }
                        }
                        nextRid += 1
                        nextImageIdx += 1
                    }
                }
            }
            ParagraphInjector.inject(documentDoc, paragraphPatches, options)
        }

        if (imagePatches.isNotEmpty() && documentDoc != null) {
            val contentTypesDoc = parsedXml["[Content_Types].xml"]
                ?: error("Patch.Image requires [Content_Types].xml in input")
            val relsDoc = parsedXml.getOrPut("word/_rels/document.xml.rels") {
                synthesizeEmptyRelsDocument()
            }
            newMedia += ImageInjector.inject(documentDoc, contentTypesDoc, relsDoc, imagePatches, options)
        }

        if (rowPatches.isNotEmpty() && documentDoc != null) {
            RowInjector.inject(documentDoc, rowPatches, options)
        }

        if (inlinePatches.isNotEmpty() && documentDoc != null) {
            // Resolve any hyperlinks AND images embedded in the
            // patch snippets BEFORE the inline replacer renders
            // them. Allocate fresh rIds from the document.xml rels
            // file (creating it if missing), append `.../hyperlink`
            // and `.../image` Relationships, and rewrite each
            // binding's resolved rId so toXml() emits the right
            // `r:id` / `r:embed`.
            val needsHyperlinkRels = inlinePatches.values.any {
                it.snippets.hyperlinkBindings().isNotEmpty()
            }
            val needsImageRels = inlinePatches.values.any {
                it.snippets.imageBindings().isNotEmpty()
            }
            if (needsHyperlinkRels || needsImageRels) {
                val relsDoc = parsedXml.getOrPut("word/_rels/document.xml.rels") {
                    synthesizeEmptyRelsDocument()
                }
                var nextRid = io.docxkt.patcher.replace.RelationshipManager.nextRid(relsDoc)
                var nextImageIdx = io.docxkt.patcher.replace.RelationshipManager
                    .maxImageMediaIndex(relsDoc) + 1
                val seenExtensionsForCT = mutableSetOf<String>()
                for ((_, patch) in inlinePatches) {
                    for (binding in patch.snippets.hyperlinkBindings()) {
                        val rid = "rId$nextRid"
                        io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                            doc = relsDoc,
                            id = nextRid,
                            type = io.docxkt.patcher.replace.RelationshipManager.HYPERLINK_TYPE,
                            target = binding.target,
                            targetMode = "External",
                        )
                        patch.snippets.resolveHyperlink(binding, rid)
                        nextRid += 1
                    }
                    for (binding in patch.snippets.imageBindings()) {
                        val rid = "rId$nextRid"
                        val mediaPath = "media/image${nextImageIdx}.${binding.format.extension}"
                        io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                            doc = relsDoc,
                            id = nextRid,
                            type = io.docxkt.patcher.replace.RelationshipManager.IMAGE_TYPE,
                            target = mediaPath,
                        )
                        patch.snippets.resolveImage(binding, rid)
                        // Queue media binary + content-type default.
                        newMedia += ImageInjector.MediaEntry(
                            path = "word/$mediaPath",
                            bytes = binding.bytes,
                        )
                        if (seenExtensionsForCT.add(binding.format.extension)) {
                            val contentTypesDoc = parsedXml["[Content_Types].xml"]
                            if (contentTypesDoc != null) {
                                io.docxkt.patcher.replace.ContentTypesManager.addDefaultExtension(
                                    doc = contentTypesDoc,
                                    extension = binding.format.extension,
                                    contentType = binding.format.mimeType,
                                )
                            }
                        }
                        nextRid += 1
                        nextImageIdx += 1
                    }
                }
            }
            io.docxkt.patcher.replace.ParagraphInlineReplacer.replace(documentDoc, inlinePatches, options)
        }

        // Apply patches to header / footer / footnote / endnote /
        // comment parts as well. Mirrors upstream
        // `from-docx.ts:245` which walks every `word/*.xml` part
        // (excluding rels) and runs the same per-part patch logic
        // for all five patch types. Hyperlink/image rIds for these
        // parts go on their OWN `word/_rels/{part}.xml.rels` file
        // (creating if missing).
        for ((path, partDoc) in parsedXml.toMap().toList()) {
            if (path == "word/document.xml") continue
            if (!isPatchablePart(path)) continue

            if (textPatches.isNotEmpty()) {
                TokenReplacer.replace(partDoc, textPatches, options)
            }
            if (paragraphPatches.isNotEmpty()) {
                resolveParagraphSnippetBindings(
                    paragraphPatches = paragraphPatches,
                    parsedXml = parsedXml,
                    partRelsPath = relsPathFor(path),
                    newMedia = newMedia,
                )
                ParagraphInjector.inject(partDoc, paragraphPatches, options)
            }
            if (imagePatches.isNotEmpty()) {
                val partRelsDoc = parsedXml.getOrPut(relsPathFor(path)) {
                    synthesizeEmptyRelsDocument()
                }
                val contentTypesDoc = parsedXml["[Content_Types].xml"]
                    ?: error("Patch.Image requires [Content_Types].xml in input")
                newMedia += ImageInjector.inject(
                    documentDoc = partDoc,
                    contentTypesDoc = contentTypesDoc,
                    relsDoc = partRelsDoc,
                    patches = imagePatches,
                    options = options,
                )
            }
            if (rowPatches.isNotEmpty()) {
                RowInjector.inject(partDoc, rowPatches, options)
            }
            if (inlinePatches.isNotEmpty()) {
                resolveInlineSnippetBindings(
                    inlinePatches = inlinePatches,
                    parsedXml = parsedXml,
                    partRelsPath = relsPathFor(path),
                    newMedia = newMedia,
                )
                io.docxkt.patcher.replace.ParagraphInlineReplacer.replace(partDoc, inlinePatches, options)
            }
        }

        // Emit output entries: parts (XML re-serialized, binaries
        // passed through) + any synthesized XML parts (e.g.
        // document.xml.rels created lazily for image patches) +
        // new media entries from image patches.
        val outputEntries = mutableListOf<DocxPackager.Entry>()
        val emittedPaths = mutableSetOf<String>()
        for ((path, bytes) in parts) {
            val outBytes = if (isXmlPart(path)) {
                OoxmlWriter.serialize(parsedXml.getValue(path))
            } else {
                bytes
            }
            outputEntries += DocxPackager.Entry(path = path, bytes = outBytes)
            emittedPaths += path
        }
        // Synthesized XML parts that didn't exist in the input.
        for ((path, doc) in parsedXml) {
            if (path !in emittedPaths) {
                outputEntries += DocxPackager.Entry(path = path, bytes = OoxmlWriter.serialize(doc))
            }
        }
        for (entry in newMedia) {
            outputEntries += DocxPackager.Entry(path = entry.path, bytes = entry.bytes)
        }

        return DocxPackager.toByteArray(outputEntries)
    }

    private fun isXmlPart(path: String): Boolean =
        path.endsWith(".xml") || path.endsWith(".rels")

    /**
     * Paragraph-bearing OOXML parts that the cross-part loop walks
     * for patches. Upstream's `from-docx.ts:245` runs the replacer
     * on every `word/<part>.xml` entry; we narrow to the parts that
     * actually carry markup users typically patch (header, footer,
     * footnotes, endnotes, comments) and skip non-paragraph parts
     * (settings.xml, fontTable.xml, theme, styles.xml,
     * numbering.xml) that the replacers would no-op on anyway.
     */
    private fun isPatchablePart(path: String): Boolean {
        if (!path.startsWith("word/")) return false
        if (path.contains("/_rels/")) return false
        if (!path.endsWith(".xml")) return false
        return path.startsWith("word/header") ||
            path.startsWith("word/footer") ||
            path == "word/footnotes.xml" ||
            path == "word/endnotes.xml" ||
            path == "word/comments.xml"
    }

    /**
     * Re-resolve the snippet bindings on every paragraph patch
     * against [partRelsPath]. Symmetric to
     * [resolveInlineSnippetBindings] but for `Patch.Paragraphs`.
     * Allocates per-part rIds and updates each snippet's mutable
     * HyperlinkSlot / ImageSlot so the next [ParagraphInjector.inject]
     * call emits the right `r:id` / `r:embed`.
     */
    private fun resolveParagraphSnippetBindings(
        paragraphPatches: Map<String, Patch.Paragraphs>,
        parsedXml: MutableMap<String, Document>,
        partRelsPath: String,
        newMedia: MutableList<ImageInjector.MediaEntry>,
    ) {
        val needsHyperlinkRels = paragraphPatches.values.any {
            it.snippets.hyperlinkBindings().isNotEmpty()
        }
        val needsImageRels = paragraphPatches.values.any {
            it.snippets.imageBindings().isNotEmpty()
        }
        if (!needsHyperlinkRels && !needsImageRels) return
        val relsDoc = parsedXml.getOrPut(partRelsPath) {
            synthesizeEmptyRelsDocument()
        }
        var nextRid = io.docxkt.patcher.replace.RelationshipManager.nextRid(relsDoc)
        var nextImageIdx = io.docxkt.patcher.replace.RelationshipManager
            .maxImageMediaIndex(relsDoc) + 1
        val seenExt = mutableSetOf<String>()
        for ((_, patch) in paragraphPatches) {
            for (binding in patch.snippets.hyperlinkBindings()) {
                val rid = "rId$nextRid"
                io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                    doc = relsDoc,
                    id = nextRid,
                    type = io.docxkt.patcher.replace.RelationshipManager.HYPERLINK_TYPE,
                    target = binding.target,
                    targetMode = "External",
                )
                patch.snippets.resolveHyperlink(binding, rid)
                nextRid += 1
            }
            for (binding in patch.snippets.imageBindings()) {
                val rid = "rId$nextRid"
                val mediaPath = "media/image${nextImageIdx}.${binding.format.extension}"
                io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                    doc = relsDoc,
                    id = nextRid,
                    type = io.docxkt.patcher.replace.RelationshipManager.IMAGE_TYPE,
                    target = mediaPath,
                )
                patch.snippets.resolveImage(binding, rid)
                newMedia += ImageInjector.MediaEntry(
                    path = "word/$mediaPath",
                    bytes = binding.bytes,
                )
                if (seenExt.add(binding.format.extension)) {
                    val ctDoc = parsedXml["[Content_Types].xml"]
                    if (ctDoc != null) {
                        io.docxkt.patcher.replace.ContentTypesManager.addDefaultExtension(
                            doc = ctDoc,
                            extension = binding.format.extension,
                            contentType = binding.format.mimeType,
                        )
                    }
                }
                nextRid += 1
                nextImageIdx += 1
            }
        }
    }

    private fun synthesizeEmptyRelsDocument(): Document {
        val xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="${io.docxkt.xml.Namespaces.PACKAGE_RELATIONSHIPS_NAMESPACE}"></Relationships>"""
        return OoxmlParser.parse(xml.toByteArray(Charsets.UTF_8))
    }

    /**
     * Given a patchable XML part path (e.g. `word/header1.xml`),
     * return the path of its relationships file
     * (`word/_rels/header1.xml.rels`).
     */
    private fun relsPathFor(partPath: String): String {
        val lastSlash = partPath.lastIndexOf('/')
        val parent = partPath.substring(0, lastSlash)
        val name = partPath.substring(lastSlash + 1)
        return "$parent/_rels/$name.rels"
    }

    /**
     * Re-resolve the snippet bindings on every inline patch
     * against [partRelsPath]. Allocates per-part rIds and updates
     * each snippet's mutable HyperlinkSlot / ImageSlot so the next
     * [io.docxkt.patcher.replace.ParagraphInlineReplacer.replace]
     * call emits the right `r:id` / `r:embed`.
     */
    private fun resolveInlineSnippetBindings(
        inlinePatches: Map<String, Patch.ParagraphInline>,
        parsedXml: MutableMap<String, Document>,
        partRelsPath: String,
        newMedia: MutableList<ImageInjector.MediaEntry>,
    ) {
        val needsHyperlinkRels = inlinePatches.values.any {
            it.snippets.hyperlinkBindings().isNotEmpty()
        }
        val needsImageRels = inlinePatches.values.any {
            it.snippets.imageBindings().isNotEmpty()
        }
        if (!needsHyperlinkRels && !needsImageRels) return
        val relsDoc = parsedXml.getOrPut(partRelsPath) {
            synthesizeEmptyRelsDocument()
        }
        var nextRid = io.docxkt.patcher.replace.RelationshipManager.nextRid(relsDoc)
        var nextImageIdx = io.docxkt.patcher.replace.RelationshipManager
            .maxImageMediaIndex(relsDoc) + 1
        val seenExt = mutableSetOf<String>()
        for ((_, patch) in inlinePatches) {
            for (binding in patch.snippets.hyperlinkBindings()) {
                val rid = "rId$nextRid"
                io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                    doc = relsDoc,
                    id = nextRid,
                    type = io.docxkt.patcher.replace.RelationshipManager.HYPERLINK_TYPE,
                    target = binding.target,
                    targetMode = "External",
                )
                patch.snippets.resolveHyperlink(binding, rid)
                nextRid += 1
            }
            for (binding in patch.snippets.imageBindings()) {
                val rid = "rId$nextRid"
                val mediaPath = "media/image${nextImageIdx}.${binding.format.extension}"
                io.docxkt.patcher.replace.RelationshipManager.addRelationship(
                    doc = relsDoc,
                    id = nextRid,
                    type = io.docxkt.patcher.replace.RelationshipManager.IMAGE_TYPE,
                    target = mediaPath,
                )
                patch.snippets.resolveImage(binding, rid)
                newMedia += ImageInjector.MediaEntry(
                    path = "word/$mediaPath",
                    bytes = binding.bytes,
                )
                if (seenExt.add(binding.format.extension)) {
                    val ctDoc = parsedXml["[Content_Types].xml"]
                    if (ctDoc != null) {
                        io.docxkt.patcher.replace.ContentTypesManager.addDefaultExtension(
                            doc = ctDoc,
                            extension = binding.format.extension,
                            contentType = binding.format.mimeType,
                        )
                    }
                }
                nextRid += 1
                nextImageIdx += 1
            }
        }
    }

    /**
     * Mirror upstream's `from-docx.ts` post-load pass on
     * `word/document.xml`:
     *
     *  1. Ensure `xmlns:mc`, `xmlns:wp`, `xmlns:r`, `xmlns:w15`,
     *     `xmlns:m` declarations are present (with their canonical
     *     URIs). Existing entries keep their position; missing ones
     *     are appended in the order upstream iterates the
     *     `DocumentAttributeNamespaces` keys (mc, wp, r, w15, m).
     *  2. Append `" w15"` to `mc:Ignorable` (or initialise it to
     *     `"w15"` when missing). Upstream does this unconditionally,
     *     producing the trailing duplicate seen in real templates.
     *
     * Mutations propagate to [io.docxkt.patcher.io.AttrSourceOrder]
     * so the writer emits canonical entries in their existing slot
     * (or appends them) rather than alongside the platform DOM's
     * alphabetised xmlns:* iteration order.
     */
    private fun ensureDocumentNamespaces(doc: Document) {
        val root = doc.documentElement ?: return
        val orderList =
            io.docxkt.patcher.io.AttrSourceOrder.get(root)?.toMutableList()
                ?: mutableListOf()

        val canonicalNs = listOf(
            "mc" to io.docxkt.xml.Namespaces.MARKUP_COMPATIBILITY,
            "wp" to io.docxkt.xml.Namespaces.WORDPROCESSING_DRAWING,
            "r" to io.docxkt.xml.Namespaces.RELATIONSHIPS_OFFICE_DOCUMENT,
            "w15" to io.docxkt.xml.Namespaces.WORDML_2012,
            "m" to io.docxkt.xml.Namespaces.MATH,
        )
        for ((prefix, uri) in canonicalNs) {
            val attrName = "xmlns:$prefix"
            root.setAttribute(attrName, uri)
            val idx = orderList.indexOfFirst { it.first == attrName }
            if (idx >= 0) {
                orderList[idx] = attrName to uri
            } else {
                orderList += attrName to uri
            }
        }
        val existing = root.getAttribute("mc:Ignorable") ?: ""
        val updated = (if (existing.isEmpty()) "w15" else "$existing w15").trim()
        root.setAttribute("mc:Ignorable", updated)
        val mcIdx = orderList.indexOfFirst { it.first == "mc:Ignorable" }
        if (mcIdx >= 0) {
            orderList[mcIdx] = "mc:Ignorable" to updated
        } else {
            orderList += "mc:Ignorable" to updated
        }
        io.docxkt.patcher.io.AttrSourceOrder.put(root, orderList)
    }
}

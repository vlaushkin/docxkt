// No upstream analogue — Kotlin idiom for the patcher cross-module
// boundary. Wraps a list of internal Paragraph and Table instances
// behind a public-stable type, exposing only the rendered XML.
package io.docxkt.api

import io.docxkt.dsl.DocumentContext
import io.docxkt.dsl.DocxktDsl
import io.docxkt.dsl.ParagraphScope
import io.docxkt.dsl.TableScope
import io.docxkt.model.hyperlink.HyperlinkSlot
import io.docxkt.xml.XmlComponent

/**
 * Standalone paragraphs (or tables) for use as input to the
 * patcher's `Patch.Paragraphs` patch type. Built via the
 * [paragraphs] top-level DSL.
 *
 * Hyperlinks / images registered inside the paragraphs carry
 * unresolved slots; the patcher walks [hyperlinkBindings] and
 * [imageBindings], allocates per-part rIds, and calls
 * [resolveHyperlink] / [resolveImage] before [toXml].
 */
public class ParagraphSnippets internal constructor(
    internal val items: List<XmlComponent>,
    internal val hyperlinkSlotsList: List<HyperlinkSlot>,
    internal val imageEntriesList: List<RunSnippets.ImageEntry>,
) {
    /**
     * Serialize each top-level item (Paragraph or Table) to its
     * `<w:p>…</w:p>` / `<w:tbl>…</w:tbl>` XML form.
     */
    public fun toXml(): List<String> = items.map { item ->
        StringBuilder().apply { item.appendXml(this) }.toString()
    }

    public val size: Int get() = items.size

    /** Public view of unresolved hyperlinks. */
    public fun hyperlinkBindings(): List<HyperlinkBinding> =
        hyperlinkSlotsList.mapIndexed { idx, slot ->
            HyperlinkBinding(target = slot.target, token = idx)
        }

    public fun resolveHyperlink(binding: HyperlinkBinding, rid: String) {
        hyperlinkSlotsList[binding.token].resolvedRid = rid
    }

    /** Public view of unresolved image insertions. */
    public fun imageBindings(): List<ImageBinding> =
        imageEntriesList.mapIndexed { idx, entry ->
            ImageBinding(
                bytes = entry.image.bytes,
                widthEmus = entry.image.widthEmus,
                heightEmus = entry.image.heightEmus,
                format = entry.image.format,
                token = idx,
            )
        }

    public fun resolveImage(binding: ImageBinding, rid: String) {
        imageEntriesList[binding.token].slot.resolvedRid = rid
    }
}

/**
 * Top-level DSL — build a list of standalone paragraphs (and
 * tables) without the surrounding [Document] / `<w:body>` context.
 *
 * ```
 * val snippets = paragraphs {
 *     paragraph { text("Hello") }
 *     paragraph {
 *         text("Visit ")
 *         hyperlink("https://example.com") { text("link") }
 *     }
 *     table { row { cell { paragraph { text("cell") } } } }
 * }
 * ```
 *
 * Hyperlinks and images are admissible — slots collected during
 * the build are exposed via [ParagraphSnippets.hyperlinkBindings]
 * / [ParagraphSnippets.imageBindings] for the patcher to resolve.
 */
public fun paragraphs(configure: ParagraphSnippetsScope.() -> Unit): ParagraphSnippets {
    val scope = ParagraphSnippetsScope()
    scope.configure()
    return ParagraphSnippets(
        items = scope.buildItems(),
        hyperlinkSlotsList = scope.hyperlinkSlots(),
        imageEntriesList = scope.imageEntries(),
    )
}

@DocxktDsl
public class ParagraphSnippetsScope internal constructor() {
    private val items = mutableListOf<XmlComponent>()
    private val context = DocumentContext()

    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        items += scope.build()
    }

    /**
     * Add a `<w:tbl>` snippet at the top level. Used by patches
     * whose `PatchType.DOCUMENT` payload is a table rather than
     * a paragraph.
     */
    public fun table(configure: TableScope.() -> Unit) {
        val scope = TableScope(context)
        scope.configure()
        items += scope.build()
    }

    internal fun buildItems(): List<XmlComponent> = items.toList()

    internal fun hyperlinkSlots(): List<HyperlinkSlot> = context.hyperlinks()

    internal fun imageEntries(): List<RunSnippets.ImageEntry> =
        context.images().map { ctxEntry ->
            RunSnippets.ImageEntry(image = ctxEntry.image, slot = ctxEntry.slot)
        }
}

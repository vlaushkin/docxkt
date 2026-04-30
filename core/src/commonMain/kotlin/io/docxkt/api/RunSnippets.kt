// No upstream analogue — Kotlin idiom for the patcher's
// PARAGRAPH-inline patch type. Wraps a list of internal Run
// and Hyperlink instances behind a public-stable type, exposing
// only the rendered XML.
package io.docxkt.api

import io.docxkt.dsl.DocumentContext
import io.docxkt.dsl.DocxktDsl
import io.docxkt.dsl.RunScope
import io.docxkt.model.drawing.Drawing
import io.docxkt.model.drawing.Image
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.model.drawing.ImageSlot
import io.docxkt.model.hyperlink.Hyperlink
import io.docxkt.model.hyperlink.HyperlinkSlot
import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text
import io.docxkt.xml.XmlComponent

/**
 * Standalone formatted runs (or hyperlinks wrapping runs) for
 * use as input to the patcher's `Patch.ParagraphInline` patch
 * type. Built via the [runs] top-level DSL.
 *
 * Hyperlinks carry an unresolved [HyperlinkSlot]; the patcher
 * walks [hyperlinkBindings], allocates a per-part rId, and
 * calls [resolveHyperlink] before [toXml].
 */
public class RunSnippets internal constructor(
    internal val items: List<XmlComponent>,
    internal val hyperlinkSlotsList: List<HyperlinkSlot>,
    internal val imageEntriesList: List<ImageEntry> = emptyList(),
) {
    /**
     * Serialize each top-level item (Run or Hyperlink) to its
     * `<w:r>…</w:r>` or `<w:hyperlink>…</w:hyperlink>` XML form.
     * Call [resolveHyperlink] for every binding before invoking
     * this — emitting an unresolved hyperlink throws.
     */
    public fun toXml(): List<String> = items.map { item ->
        StringBuilder().apply { item.appendXml(this) }.toString()
    }

    public val size: Int get() = items.size

    /**
     * Public view of the unresolved hyperlinks in this snippet.
     * Each binding carries the target URL plus an opaque [token]
     * the patcher uses to call [resolveHyperlink].
     */
    public fun hyperlinkBindings(): List<HyperlinkBinding> =
        hyperlinkSlotsList.mapIndexed { idx, slot ->
            HyperlinkBinding(target = slot.target, token = idx)
        }

    /**
     * Set the resolved rId on the hyperlink referenced by
     * [binding]. Subsequent calls to [toXml] render the
     * hyperlink with `r:id="{rid}"`.
     */
    public fun resolveHyperlink(binding: HyperlinkBinding, rid: String) {
        hyperlinkSlotsList[binding.token].resolvedRid = rid
    }

    /**
     * Public view of the unresolved image insertions in this
     * snippet. Each binding carries the image bytes + format
     * + dimensions; the patcher must allocate a per-part rId,
     * write the binary to `word/media/{filename}.{ext}`, and
     * call [resolveImage] before [toXml].
     */
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

    /**
     * Set the resolved rId on the image referenced by [binding].
     * The next [toXml] call emits a `<a:blip r:embed="{rid}"/>`
     * carrying that rId.
     */
    public fun resolveImage(binding: ImageBinding, rid: String) {
        imageEntriesList[binding.token].slot.resolvedRid = rid
    }

    /** Internal record pairing one image with its mutable rId slot. */
    internal data class ImageEntry(val image: Image, val slot: ImageSlot)
}

/**
 * Reference to one unresolved image insertion in a [RunSnippets].
 * The patcher uses this to allocate a part-scoped image rId,
 * write the media binary, and call [RunSnippets.resolveImage].
 */
public data class ImageBinding(
    val bytes: ByteArray,
    val widthEmus: Int,
    val heightEmus: Int,
    val format: ImageFormat,
    val token: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ImageBinding) return false
        return token == other.token &&
            widthEmus == other.widthEmus &&
            heightEmus == other.heightEmus &&
            format == other.format &&
            bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        var result = token
        result = 31 * result + widthEmus
        result = 31 * result + heightEmus
        result = 31 * result + format.hashCode()
        result = 31 * result + bytes.contentHashCode()
        return result
    }
}

/**
 * Reference to one unresolved hyperlink in a [RunSnippets]
 * instance. The [token] is opaque — pass back to
 * [RunSnippets.resolveHyperlink] once a per-part rId has been
 * allocated.
 */
public data class HyperlinkBinding(
    val target: String,
    val token: Int,
)

/**
 * Top-level DSL — build a list of standalone runs (and
 * hyperlinks) without the surrounding `<w:p>` context.
 *
 * ```
 * val snippets = runs {
 *     run("Max")
 *     run("Bar") { bold = true }
 *     hyperlink("https://example.com") { run("link text") }
 * }
 * ```
 */
public fun runs(configure: RunSnippetsScope.() -> Unit): RunSnippets {
    val scope = RunSnippetsScope()
    scope.configure()
    return RunSnippets(
        items = scope.buildItems(),
        hyperlinkSlotsList = scope.hyperlinkSlots(),
        imageEntriesList = scope.imageEntries(),
    )
}

@DocxktDsl
public class RunSnippetsScope internal constructor() {
    private val items = mutableListOf<XmlComponent>()
    private val context = DocumentContext()
    private val slots = mutableListOf<HyperlinkSlot>()
    private val imageList = mutableListOf<RunSnippets.ImageEntry>()

    /** Add a plain text run (no formatting). */
    public fun run(value: String) {
        items += Run(children = listOf(Text(value)))
    }

    /** Add a formatted text run. Configure block applies run-level properties. */
    public fun run(value: String, configure: RunScope.() -> Unit) {
        val scope = RunScope(context)
        scope.configure()
        items += Run(
            children = scope.leadingChildren() +
                listOf<XmlComponent>(Text(value)) +
                scope.extraChildren(),
            properties = scope.buildProperties(),
        )
    }

    /**
     * Add an external hyperlink wrapping the runs configured in
     * [configure]. The patcher resolves the rId at apply time
     * and adds a `.../hyperlink` relationship with
     * `TargetMode="External"` to the part being patched.
     */
    public fun hyperlink(target: String, configure: HyperlinkRunsScope.() -> Unit) {
        val sub = HyperlinkRunsScope(context)
        sub.configure()
        val slot = HyperlinkSlot(target = target)
        slots += slot
        items += Hyperlink.external(slot = slot, runs = sub.build())
    }

    /**
     * Add an inline image. The patcher resolves the rId at apply
     * time and writes the binary to `word/media/{filename}.{ext}`,
     * appending an image Relationship to the part's rels file.
     */
    public fun image(
        bytes: ByteArray,
        widthEmus: Int,
        heightEmus: Int,
        format: ImageFormat,
        description: String? = null,
    ) {
        val image = Image(
            bytes = bytes,
            widthEmus = widthEmus,
            heightEmus = heightEmus,
            format = format,
            description = description,
        )
        val slot = ImageSlot()
        imageList += RunSnippets.ImageEntry(image = image, slot = slot)
        items += Run(
            children = listOf(
                Drawing(
                    embedSlot = slot,
                    widthEmus = widthEmus,
                    heightEmus = heightEmus,
                    description = description,
                ),
            ),
        )
    }

    internal fun buildItems(): List<XmlComponent> = items.toList()
    internal fun hyperlinkSlots(): List<HyperlinkSlot> = slots.toList()
    internal fun imageEntries(): List<RunSnippets.ImageEntry> = imageList.toList()
}

/**
 * Builder receiver for hyperlink-wrapped runs. Same surface as
 * the run-only path of [RunSnippetsScope]; nested hyperlinks
 * are not supported (matches upstream).
 */
@DocxktDsl
public class HyperlinkRunsScope internal constructor(
    internal val context: DocumentContext,
) {
    private val list = mutableListOf<Run>()

    public fun run(value: String) {
        list += Run(children = listOf(Text(value)))
    }

    public fun run(value: String, configure: RunScope.() -> Unit) {
        val scope = RunScope(context)
        scope.configure()
        list += Run(
            children = scope.leadingChildren() +
                listOf<XmlComponent>(Text(value)) +
                scope.extraChildren(),
            properties = scope.buildProperties(),
        )
    }

    internal fun build(): List<Run> = list.toList()
}

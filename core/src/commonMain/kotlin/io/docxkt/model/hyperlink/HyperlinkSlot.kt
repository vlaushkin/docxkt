// No upstream analogue — upstream's ExternalHyperlink is rewritten to a
// ConcreteHyperlink with a resolved rId during `prep-for-xml.ts`. We
// defer resolution via a shared slot, same pattern as ImageSlot /
// NumberingReferenceSlot.
package io.docxkt.model.hyperlink

/**
 * Mutable rId placeholder for an external hyperlink.
 *
 * The DSL registers a hyperlink call site at `ParagraphScope.hyperlink`
 * time with only the target URL known. `Document.buildDocument()` fills
 * [resolvedRid] after the allocator runs (canonical order: header →
 * footer → numbering → styles → hyperlink → images).
 *
 * Trying to emit a [Hyperlink] whose slot is unresolved is a bug.
 */
internal class HyperlinkSlot(
    val target: String,
) {
    var resolvedRid: String? = null

    val rid: String
        get() = resolvedRid
            ?: error("HyperlinkSlot(target='$target') was serialized before Document.buildDocument() resolved rIds")
}

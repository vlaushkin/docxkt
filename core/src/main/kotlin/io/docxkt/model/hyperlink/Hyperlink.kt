// Port of: src/file/paragraph/links/hyperlink.ts (ConcreteHyperlink) —
// covers both the external (r:id) and internal (w:anchor = bookmark
// name) variants.
package io.docxkt.model.hyperlink

import io.docxkt.model.paragraph.run.Run
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * A `<w:hyperlink>` wrapping one or more runs.
 *
 * Two variants:
 * - **External URL**: carries [slot] with an allocated `rId`
 *   pointing at a `.../relationships/hyperlink` rel with
 *   `TargetMode="External"`. Emitted as
 *   `<w:hyperlink w:history="1" r:id="rIdN">`.
 * - **Internal anchor**: carries [anchor] — a bookmark name
 *   registered elsewhere in the same document. No rel, no rId.
 *   Emitted as `<w:hyperlink w:history="1" w:anchor="bookmarkName">`.
 *
 * Attribute order on the opening tag follows upstream's
 * `ConcreteHyperlink` constructor-options object population order:
 * `w:history` first (always), then whichever of `r:id` / `w:anchor`
 * is set. Goldens depend on this order.
 *
 * `w:history="1"` is hardcoded — upstream never emits `"0"`.
 */
internal class Hyperlink private constructor(
    val slot: HyperlinkSlot?,
    val anchor: String?,
    val runs: List<Run>,
) : XmlComponent("w:hyperlink") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:hyperlink",
            "w:history" to "1",
            "w:anchor" to anchor,
            "r:id" to slot?.rid,
        )
        for (r in runs) r.appendXml(out)
        out.closeElement("w:hyperlink")
    }

    companion object {
        /** External-URL variant. */
        fun external(slot: HyperlinkSlot, runs: List<Run>): Hyperlink =
            Hyperlink(slot = slot, anchor = null, runs = runs)

        /** Internal-anchor variant. */
        fun internal(anchor: String, runs: List<Run>): Hyperlink =
            Hyperlink(slot = null, anchor = anchor, runs = runs)
    }
}

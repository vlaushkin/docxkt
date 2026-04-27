// Port of: src/file/paragraph/formatting/unordered-list.ts (NumberProperties).
package io.docxkt.model.numbering

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * Mutable placeholder for a `<w:numPr>` reference that hasn't been
 * resolved to a concrete numId yet.
 *
 * The DSL registers `paragraph { numbering(ref, level) }` call
 * sites via a fresh slot; `Document.buildDocument()` walks every
 * registered list template, assigns numIds, then fills every
 * slot's [resolvedNumId] before any part serializes.
 *
 * Trying to emit a `NumberingReference` whose slot is unresolved
 * is a loud error — either the DSL's resolution pass didn't run
 * or the user referenced a list template that was never
 * registered.
 */
internal class NumberingReferenceSlot(
    val reference: String,
    val level: Int,
    val instance: Int = 0,
    /**
     * Refs registered from a header/footer scope mirror upstream's
     * prepForXml ordering quirk — Numbering serializes BEFORE H/F,
     * so user concretes for H/F-only refs are never emitted. The
     * body XML still references the numId, dangling. We mirror by
     * skipping the concrete when [inHeaderFooter] is true.
     */
    val inHeaderFooter: Boolean = false,
) {
    var resolvedNumId: Int? = null

    val numId: Int
        get() = resolvedNumId
            ?: error("NumberingReferenceSlot('$reference', level=$level, instance=$instance) never resolved — was the list template registered?")
}

/**
 * Resolved `<w:numPr>` content. Reads its numId from the slot at
 * emit time, so the same slot can be filled later by
 * `Document.buildDocument()` after the allocator has run.
 *
 * Child order: `<w:ilvl/>` then `<w:numId/>` (upstream-canonical).
 */
internal class NumberingReference(
    val slot: NumberingReferenceSlot,
) : XmlComponent("w:numPr") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:numPr")
        out.selfClosingElement("w:ilvl", "w:val" to slot.level.toString())
        out.selfClosingElement("w:numId", "w:val" to slot.numId.toString())
        out.closeElement("w:numPr")
    }
}

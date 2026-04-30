// No upstream analogue — deferred-resolution placeholder for mid-body
// section breaks that may carry per-section headers/footers.
package io.docxkt.dsl

import io.docxkt.xml.XmlComponent

/**
 * Placeholder block emitted by `DocumentScope.sectionBreak { … }`.
 * Holds the [scope] until `buildDocument()` walks all pending
 * section breaks, allocates rIds for each section's H/F, and
 * replaces the placeholder with an actual paragraph carrying a
 * resolved [io.docxkt.model.section.SectionProperties].
 *
 * Calling [appendXml] is a programming error — the build pipeline
 * always substitutes the placeholder before XML emission.
 */
internal class PendingSectionBreak(
    val scope: SectionBreakScope,
) : XmlComponent("__pending_section_break__") {

    override fun appendXml(out: Appendable) {
        error(
            "PendingSectionBreak.appendXml called — buildDocument() did not " +
                "resolve this placeholder. This is a build-pipeline bug.",
        )
    }
}

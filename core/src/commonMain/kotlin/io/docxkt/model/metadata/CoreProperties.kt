// Port of: src/file/core-properties/properties.ts (CoreProperties).
// We emit the Dublin-Core subset + required timestamps. OOXML's
// CT_CoreProperties admits more elements (category, contentStatus,
// identifier, language, lastPrinted, version) — not yet supported.
package io.docxkt.model.metadata

import io.docxkt.util.nowIso8601

/**
 * Dublin-Core metadata block for `docProps/core.xml`. All fields are
 * optional; unset fields simply don't emit their element. Even when
 * every optional field is null, the part still emits — upstream's
 * lastModifiedBy/creator default to the string "Un-named" and
 * revision defaults to "1". Two timestamps
 * (`dcterms:created` / `dcterms:modified`) always emit.
 *
 * `createdAt` / `modifiedAt` default to a fresh ISO-8601 timestamp
 * pair at construction; fixture tests override both to a sentinel
 * for byte-stable diffing.
 */
internal data class CoreProperties(
    val title: String? = null,
    val subject: String? = null,
    val creator: String? = null,
    val keywords: String? = null,
    val description: String? = null,
    val lastModifiedBy: String? = null,
    val revision: Int? = null,
    /** W3C DateTime string, e.g. `"2026-04-24T00:00:00.000Z"`. */
    val createdAt: String = nowIso8601(),
    val modifiedAt: String = createdAt,
) {
    /** Upstream emits this literal when creator is omitted. */
    val effectiveCreator: String get() = creator ?: "Un-named"

    /** Same literal for lastModifiedBy. */
    val effectiveLastModifiedBy: String get() = lastModifiedBy ?: "Un-named"

    /** Upstream defaults revision to 1. */
    val effectiveRevision: Int get() = revision ?: 1
}

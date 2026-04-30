// Port of: src/file/relationships/relationships.ts (rId counter logic).
package io.docxkt.part

/**
 * Allocates `rId{N}` identifiers for document-scoped relationships.
 *
 * Layout matches upstream's order: implicit document-level rels
 * (styles, numbering, footnotes, endnotes, settings, comments)
 * occupy a fixed prefix of the rId space; user-visible rels (header,
 * footer, image, hyperlink) start AFTER the prefix.
 *
 * Even when an implicit rel is absent (e.g. no comments registered),
 * its slot is still consumed — body XML rIds for header/footer/etc.
 * stay byte-equal upstream regardless of which implicit rels are
 * present. The rels file may have gaps; OOXML accepts that.
 *
 * Contract:
 *   1. Construct, then call [nextId] in the order user-visible rels
 *      should appear. Each call returns a fresh `rId{N}` starting at
 *      `IMPLICIT_PREFIX_SIZE + 1` (i.e. `rId7`).
 *   2. Implicit rels claim their slot via [implicitRid]; the same
 *      name always returns the same rId (idempotent).
 *
 * Not thread-safe; callers serialize access.
 */
internal class RelationshipAllocator {
    /**
     * Implicit-prefix order matches upstream's
     * `addDefaultRelationships()` push order (`file.ts:320-378`).
     */
    private val implicitOrder: List<String> = listOf(
        "styles", "numbering", "footnotes", "endnotes", "settings", "comments",
    )

    /** rIds 1..6 reserved for the implicit prefix. Dynamic starts at 7. */
    private var counter: Int = IMPLICIT_PREFIX_SIZE

    /** Next fresh rId for a user-visible rel. */
    fun nextId(): String {
        counter += 1
        return "rId$counter"
    }

    /**
     * Return the rId for a known implicit rel (e.g. `"styles"`,
     * `"numbering"`). The rId is fixed by [implicitOrder] — stable
     * regardless of call order or absence of other implicit rels.
     */
    fun implicitRid(name: String): String {
        val idx = implicitOrder.indexOf(name)
        require(idx >= 0) { "Unknown implicit rel name: $name" }
        return "rId${idx + 1}"
    }

    /** Count of dynamic rIds issued (excluding the reserved prefix). */
    val allocatedCount: Int get() = counter - IMPLICIT_PREFIX_SIZE

    internal companion object {
        /**
         * Reserved-prefix size for implicit document-level rels.
         * Matches upstream's six default rels (styles, numbering,
         * footnotes, endnotes, settings, comments). Dynamic rels
         * start at `rId(IMPLICIT_PREFIX_SIZE + 1)`.
         */
        const val IMPLICIT_PREFIX_SIZE: Int = 6
    }
}

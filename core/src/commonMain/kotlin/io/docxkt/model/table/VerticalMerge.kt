// Port of: src/file/table/table-cell/table-cell-components.ts (VerticalMergeType).
package io.docxkt.model.table

/**
 * Vertical-merge mode for `<w:vMerge w:val="...">`.
 *
 * - `RESTART` — top cell of a merge region.
 * - `CONTINUE` — placeholder cell covered by the merge above.
 *
 * Upstream always emits `w:val`, even though OOXML allows the
 * `continue` value to be defaulted; we match upstream.
 */
public enum class VerticalMerge(internal val wire: String) {
    RESTART("restart"),
    CONTINUE("continue"),
}

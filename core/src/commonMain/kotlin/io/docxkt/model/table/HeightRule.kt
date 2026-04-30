// Port of: src/file/table/table-row/table-row-height.ts (HeightRule).
package io.docxkt.model.table

/**
 * Row-height rule for `<w:trHeight w:hRule="...">`.
 *
 * - `AUTO` — compute from content, ignore the height value.
 * - `ATLEAST` — grow for tall content, never shrink below the value.
 * - `EXACT` — exactly this many twips; tall content clips.
 */
public enum class HeightRule(internal val wire: String) {
    AUTO("auto"),
    ATLEAST("atLeast"),
    EXACT("exact"),
}

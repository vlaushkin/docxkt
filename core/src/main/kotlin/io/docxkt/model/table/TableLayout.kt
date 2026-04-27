// Port of: src/file/table/table-properties/table-layout.ts (TableLayoutType).
package io.docxkt.model.table

/**
 * Table layout algorithm for `<w:tblLayout w:type="...">`.
 *
 * - `FIXED` — columns exactly the widths declared in `<w:tblGrid>` / `<w:tcW>`.
 * - `AUTOFIT` — Word reflows columns to fit content.
 */
public enum class TableLayout(internal val wire: String) {
    FIXED("fixed"),
    AUTOFIT("autofit"),
}

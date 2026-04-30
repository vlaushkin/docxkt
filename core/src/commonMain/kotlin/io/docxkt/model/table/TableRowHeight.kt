// Port of: src/file/table/table-row/table-row-height.ts (createTableRowHeight).
package io.docxkt.model.table

/**
 * `<w:trHeight>` value: height in twips plus an optional rule. When
 * [rule] is `null` the attribute is omitted (Word defaults to `auto`).
 */
public data class TableRowHeight(
    val value: Int,
    val rule: HeightRule? = null,
)

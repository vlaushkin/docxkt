// Port of: src/file/table/table-width.ts (WidthType + ITableWidthProperties).
package io.docxkt.model.table

/**
 * Width unit for `<w:tblW>`, `<w:tcW>`, `<w:tblInd>`.
 *
 * `DXA` and `NIL` interpret the numeric value as twips (and `NIL` ignores
 * it). `PCT` is *fiftieths of a percent* (OOXML quirk): `5000` means
 * 100%. `AUTO` leaves layout to Word and treats the value as a hint.
 */
public enum class WidthType(internal val wire: String) {
    AUTO("auto"),
    DXA("dxa"),
    NIL("nil"),
    PCT("pct"),
}

/**
 * A width value with its unit.
 *
 * Matches upstream's `ITableWidthProperties`: a size plus an optional
 * unit type (defaulting to `AUTO` when unset). Use [auto] /
 * [dxa] / [pct] / [nil] factories at call sites to document intent.
 */
public data class TableWidth(
    val size: Int,
    val type: WidthType = WidthType.AUTO,
) {
    public companion object {
        public fun auto(size: Int = 0): TableWidth = TableWidth(size, WidthType.AUTO)
        public fun dxa(twips: Int): TableWidth = TableWidth(twips, WidthType.DXA)
        /** [fiftiethsOfPercent] — `5000` = 100%. */
        public fun pct(fiftiethsOfPercent: Int): TableWidth =
            TableWidth(fiftiethsOfPercent, WidthType.PCT)
        public fun nil(): TableWidth = TableWidth(0, WidthType.NIL)
    }
}

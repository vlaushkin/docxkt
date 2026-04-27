// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.border.BorderSide
import io.docxkt.model.paragraph.Paragraph
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import io.docxkt.model.table.CellMargins
import io.docxkt.model.table.HeightRule
import io.docxkt.model.table.Table
import io.docxkt.model.table.TableBorders
import io.docxkt.model.table.TableCell
import io.docxkt.model.table.TableCellBorders
import io.docxkt.model.table.TableCellProperties
import io.docxkt.model.table.TableGrid
import io.docxkt.model.table.TableLayout
import io.docxkt.model.table.TableProperties
import io.docxkt.model.table.TableRow
import io.docxkt.model.table.TableRowHeight
import io.docxkt.model.table.TableRowProperties
import io.docxkt.model.table.TableWidth
import io.docxkt.model.table.VerticalAlignment
import io.docxkt.model.table.VerticalMerge

/**
 * Builder for the six sides of a table- or cell-level borders block.
 *
 * Each side is a nullable `var`; unset sides default to `null`.
 * `TableBorders` fills in upstream defaults for unset sides;
 * `TableCellBorders` emits only the set ones.
 */
@DocxktDsl
public class BorderSidesScope internal constructor() {
    public var top: BorderSide? = null
    public var left: BorderSide? = null
    public var bottom: BorderSide? = null
    public var right: BorderSide? = null
    public var insideHorizontal: BorderSide? = null
    public var insideVertical: BorderSide? = null

    /** `<w:start>` edge — used by `<w:tcBorders>` only, ignored on tables. */
    public var start: BorderSide? = null

    /** `<w:end>` edge — used by `<w:tcBorders>` only, ignored on tables. */
    public var end: BorderSide? = null

    /** `<w:between>` — paragraph-borders only, ignored for tables/cells. */
    public var between: BorderSide? = null
}

/**
 * Builder for a `<w:tbl>` (table).
 *
 * Mirrors the ergonomics of [ParagraphScope]: simple setters, block
 * methods for compound values, and a child scope (`row { ... }`) for
 * nested structure.
 *
 * ```kotlin
 * table {
 *     width(value = 100, type = TableWidthType.PCT)
 *     row {
 *         cell { paragraph { text("Metric") { bold = true } } }
 *         cell { paragraph { text("Value") { bold = true } } }
 *     }
 *     row {
 *         cell { paragraph { text("Active users") } }
 *         cell { paragraph { text("1,240") } }
 *     }
 * }
 * ```
 *
 * Upstream's `Table` constructor *always* emits a default `<w:tblW>`
 * (type auto, size 100) and a default `<w:tblBorders>` block with
 * SINGLE / size 4 / auto on every side. Our DSL mirrors both defaults
 * so fixtures generated via dolanmiu/docx without explicit width /
 * borders diff cleanly.
 *
 * - `width(null)` opts out of emitting `<w:tblW>`.
 * - `borders(null)` opts out of emitting `<w:tblBorders>` entirely.
 *   Calling `borders { top = BorderSide(...) }` keeps the remaining
 *   sides at upstream defaults (upstream's per-side fill-in).
 */
@DocxktDsl
public class TableScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    private val rows = mutableListOf<TableRow>()
    private var widthValue: TableWidth? = UPSTREAM_DEFAULT_TABLE_WIDTH
    private var indentValue: TableWidth? = null
    private var layoutValue: TableLayout? = null
    private var bordersValue: TableBorders? = TableBorders.DEFAULTS
    private var shadingValue: Shading? = null
    private var cellMarginsValue: CellMargins? = null
    private val columnWidthsTwips = mutableListOf<Int>()

    /**
     * Reference an externally-defined table style by id (`<w:tblStyle
     * w:val="StyleId"/>`). The style itself lives in `word/styles.xml`
     * (or an externalStyles resource) and is not registered by this DSL.
     */
    public var styleReference: String? = null

    /**
     * Visual right-to-left flag (`<w:bidiVisual/>`) — flips the visual
     * column order for RTL languages (Arabic, Hebrew). Null = inherit.
     */
    public var visuallyRightToLeft: Boolean? = null

    private var tableLookValue: io.docxkt.model.table.TableLook? = null

    /**
     * Configure `<w:tblLook>` — conditional-formatting hints used by
     * a referenced table style. Flags map 1:1 to OOXML attributes;
     * `null` (default for each flag) omits that attribute.
     */
    public fun tableLook(
        firstRow: Boolean? = null,
        lastRow: Boolean? = null,
        firstColumn: Boolean? = null,
        lastColumn: Boolean? = null,
        noHBand: Boolean? = null,
        noVBand: Boolean? = null,
    ) {
        tableLookValue = io.docxkt.model.table.TableLook(
            firstRow = firstRow,
            lastRow = lastRow,
            firstColumn = firstColumn,
            lastColumn = lastColumn,
            noHBand = noHBand,
            noVBand = noVBand,
        )
    }

    /**
     * Set the table width. Pass `null` to opt out of emitting `<w:tblW>`
     * (useful only when a downstream consumer insists on no default);
     * otherwise prefer one of the [TableWidth] factory methods.
     */
    public fun width(value: TableWidth?) {
        widthValue = value
    }

    /** Set the table-level indent (twips from the page margin). */
    public fun indent(twips: Int) {
        indentValue = TableWidth.dxa(twips)
    }

    /** Set the table-layout algorithm. */
    public fun layout(value: TableLayout) {
        layoutValue = value
    }

    /**
     * Declare the column widths of the grid (twips per column). Upstream
     * defaults to `100` per column when the caller supplies nothing;
     * passing an explicit list overrides that.
     */
    public fun columnWidths(vararg twips: Int) {
        columnWidthsTwips.clear()
        columnWidthsTwips.addAll(twips.toList())
    }

    /**
     * Configure `<w:tblBorders>`. Any side left at `null` inside the
     * block falls through to [BorderSide.UPSTREAM_DEFAULT] at emit time
     * — upstream's per-side fill-in.
     *
     * The table-level variant ignores `start` / `end` (use `left` /
     * `right` instead).
     */
    public fun borders(configure: BorderSidesScope.() -> Unit) {
        val scope = BorderSidesScope().apply(configure)
        bordersValue = TableBorders(
            top = scope.top,
            left = scope.left,
            bottom = scope.bottom,
            right = scope.right,
            insideH = scope.insideHorizontal,
            insideV = scope.insideVertical,
        )
    }

    /**
     * Opt out of emitting `<w:tblBorders>` entirely. Upstream always
     * emits the element; most callers want that, so this exists as a
     * rarely-needed escape hatch and not as an opaque `null` overload.
     */
    public fun noBorders() {
        bordersValue = null
    }

    /**
     * Emit the explicit "no borders" shape: `<w:tblBorders>` with all
     * six sides set to `style=none size=0 color=auto`. Matches
     * upstream's `TableBorders.NONE` convenience. Use when you want
     * the table to render without lines but you want the OOXML
     * declaration to be explicit (vs [noBorders] which suppresses
     * the element).
     */
    public fun bordersAllNone() {
        bordersValue = TableBorders.NONE
    }

    /** Set table-level shading. */
    public fun shading(value: Shading) {
        shadingValue = value
    }

    /**
     * Set table-level shading from pattern + optional colors. For
     * solid-fill tables, `shading(pattern = CLEAR, fill = "EEEEEE")`
     * is the idiomatic invocation.
     */
    public fun shading(pattern: ShadingPattern, color: String? = null, fill: String? = null) {
        shadingValue = Shading(pattern = pattern, color = color, fill = fill)
    }

    /**
     * Set table-level default cell margins (`<w:tblCellMar>`). Values
     * in twips. Any `null` side is omitted from the wire.
     */
    public fun cellMargins(
        top: Int? = null,
        left: Int? = null,
        bottom: Int? = null,
        right: Int? = null,
    ) {
        cellMarginsValue = CellMargins(top = top, left = left, bottom = bottom, right = right)
    }

    /** Add a row; apply [configure] to its scope. */
    public fun row(configure: TableRowScope.() -> Unit) {
        val scope = TableRowScope(context)
        scope.configure()
        rows += scope.build()
    }

    internal fun build(): Table {
        require(rows.isNotEmpty()) { "Table must contain at least one row" }
        val columnWidths = if (columnWidthsTwips.isNotEmpty()) {
            columnWidthsTwips.toList()
        } else {
            val cols = rows.maxOf { row -> row.cells.sumOf { it.gridSpanOrOne() } }
            List(cols) { UPSTREAM_DEFAULT_COLUMN_WIDTH }
        }
        val hasAnyProp = styleReference != null || visuallyRightToLeft != null ||
            widthValue != null || indentValue != null || layoutValue != null ||
            bordersValue != null || shadingValue != null ||
            (cellMarginsValue != null && !cellMarginsValue!!.isEmpty()) ||
            tableLookValue != null
        val props = if (hasAnyProp) {
            TableProperties(
                styleReference = styleReference,
                visuallyRightToLeft = visuallyRightToLeft,
                width = widthValue,
                indent = indentValue,
                layout = layoutValue,
                borders = bordersValue,
                shading = shadingValue,
                cellMargins = cellMarginsValue?.takeUnless { it.isEmpty() },
                tableLook = tableLookValue,
            )
        } else {
            null
        }
        return Table(properties = props, grid = TableGrid(columnWidths), rows = rows.toList())
    }

    private fun TableCell.gridSpanOrOne(): Int = properties?.gridSpan ?: 1

    internal companion object {
        /**
         * Upstream's `Table` constructor defaults width to `{size: 100}`
         * which in turn defaults the type to AUTO. Match exactly so
         * fixtures not specifying width diff cleanly.
         */
        val UPSTREAM_DEFAULT_TABLE_WIDTH: TableWidth = TableWidth.auto(100)

        /**
         * Upstream's fallback column width when the caller omits
         * `columnWidths`. Each column gets the same value.
         */
        const val UPSTREAM_DEFAULT_COLUMN_WIDTH: Int = 100
    }
}

/**
 * Builder for a single `<w:tr>` (table row).
 *
 * Collect cells via [cell], optionally set a fixed [height], and
 * mark the row as a header (repeats on each page break) by setting
 * [tableHeader].
 *
 * ```kotlin
 * row {
 *     tableHeader = true
 *     height(twips = 360, rule = HeightRule.AT_LEAST)
 *     cell { paragraph { text("Name") { bold = true } } }
 *     cell { paragraph { text("Value") { bold = true } } }
 * }
 * ```
 */
@DocxktDsl
public class TableRowScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    private val cells = mutableListOf<TableCell>()

    /**
     * Three-state OnOff for `<w:tblHeader/>`. `null` (default)
     * suppresses the element; `true` marks the row as a header that
     * Word repeats on every page break; `false` writes the explicit
     * `w:val="false"` form.
     */
    public var tableHeader: Boolean? = null
    private var heightValue: TableRowHeight? = null

    /** Set the row height in twips with an optional rule. */
    public fun height(twips: Int, rule: HeightRule? = null) {
        heightValue = TableRowHeight(value = twips, rule = rule)
    }

    /** Add a cell; apply [configure] to its scope. */
    public fun cell(configure: TableCellScope.() -> Unit) {
        val scope = TableCellScope(context)
        scope.configure()
        cells += scope.build()
    }

    internal fun build(): TableRow {
        require(cells.isNotEmpty()) { "TableRow must contain at least one cell" }
        val hasProp = tableHeader != null || heightValue != null
        val props = if (hasProp) {
            TableRowProperties(tableHeader = tableHeader, height = heightValue)
        } else {
            null
        }
        return TableRow(cells = cells.toList(), properties = props)
    }
}

/**
 * Builder for a single `<w:tc>` (table cell).
 *
 * Cells collect paragraphs via the embedded [ParagraphScope]. A cell
 * with no paragraphs has an empty auto-padded paragraph inserted at
 * build time — OOXML requires `<w:tc>` to end with a paragraph.
 *
 * ```kotlin
 * cell {
 *     width(2880, TableWidthType.DXA)
 *     verticalAlign(VerticalAlignment.CENTER)
 *     paragraph { text("Header") { bold = true } }
 * }
 * ```
 */
@DocxktDsl
public class TableCellScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    private val children = mutableListOf<io.docxkt.xml.XmlComponent>()
    private var widthValue: TableWidth? = null
    private var gridSpanValue: Int? = null
    private var verticalMergeValue: VerticalMerge? = null
    private var verticalAlignValue: VerticalAlignment? = null
    private var bordersValue: TableCellBorders? = null
    private var shadingValue: Shading? = null
    private var marginsValue: CellMargins? = null
    private var textDirectionValue: io.docxkt.model.table.TextDirection? = null

    /**
     * Set cell text direction (`<w:textDirection w:val="…"/>`). Used
     * for rotated text in table headers (vertical column labels) or
     * non-LTR table cells.
     */
    public fun textDirection(value: io.docxkt.model.table.TextDirection) {
        textDirectionValue = value
    }

    /** Set the cell width. */
    public fun width(value: TableWidth) {
        widthValue = value
    }

    /** Merge this cell across [columns] grid columns. Value must be ≥ 1. */
    public fun gridSpan(columns: Int) {
        require(columns >= 1) { "gridSpan must be >= 1" }
        gridSpanValue = columns
    }

    /** Mark this cell as the start (RESTART) or continuation (CONTINUE) of a vertical merge. */
    public fun verticalMerge(value: VerticalMerge) {
        verticalMergeValue = value
    }

    /** Vertical alignment of cell content. */
    public fun verticalAlign(value: VerticalAlignment) {
        verticalAlignValue = value
    }

    /**
     * Configure `<w:tcBorders>`. Unlike the table-level variant, only
     * sides explicitly set inside the block emit. `left` / `right` vs
     * `start` / `end` are the XSD-preferred names (the logical-left /
     * logical-right sides) — use the pair that matches the source.
     */
    public fun borders(configure: BorderSidesScope.() -> Unit) {
        val scope = BorderSidesScope().apply(configure)
        val anySide = scope.top != null || scope.start != null ||
            scope.left != null || scope.bottom != null ||
            scope.end != null || scope.right != null
        bordersValue = if (anySide) {
            TableCellBorders(
                top = scope.top,
                start = scope.start,
                left = scope.left,
                bottom = scope.bottom,
                end = scope.end,
                right = scope.right,
            )
        } else {
            null
        }
    }

    /** Set cell shading. */
    public fun shading(value: Shading) {
        shadingValue = value
    }

    /**
     * Set cell shading from pattern + optional colors. For solid-fill
     * cells, `shading(pattern = CLEAR, fill = "EEEEEE")` is idiomatic.
     */
    public fun shading(pattern: ShadingPattern, color: String? = null, fill: String? = null) {
        shadingValue = Shading(pattern = pattern, color = color, fill = fill)
    }

    /**
     * Set cell margins (`<w:tcMar>`). Values in twips; any `null` side
     * is omitted from the wire.
     */
    public fun margins(
        top: Int? = null,
        left: Int? = null,
        bottom: Int? = null,
        right: Int? = null,
    ) {
        val margins = CellMargins(top = top, left = left, bottom = bottom, right = right)
        // Same non-null-means-non-empty contract.
        marginsValue = if (margins.isEmpty()) null else margins
    }

    /** Add a paragraph to the cell. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        children += scope.build()
    }

    /**
     * Add a nested table to the cell. OOXML allows tables inside
     * cells as siblings to paragraphs. A cell containing a `<w:tbl>`
     * MUST end with a `<w:p/>` — at build time we auto-append an
     * empty paragraph if the last child is a table (matching
     * upstream's "cells must end in a paragraph" rule).
     */
    public fun table(configure: TableScope.() -> Unit) {
        val scope = TableScope(context)
        scope.configure()
        children += scope.build()
    }

    internal fun build(): TableCell {
        // OOXML rule: <w:tc> must end with a paragraph. Pad if empty
        // OR if the last child is a non-paragraph (e.g. nested table).
        val effectiveChildren = if (
            children.isEmpty() || children.last() !is Paragraph
        ) {
            children + Paragraph(children = emptyList())
        } else {
            children.toList()
        }
        val hasProp = widthValue != null || gridSpanValue != null ||
            verticalMergeValue != null || verticalAlignValue != null ||
            bordersValue != null || shadingValue != null || marginsValue != null ||
            textDirectionValue != null
        val props = if (hasProp) {
            TableCellProperties(
                width = widthValue,
                gridSpan = gridSpanValue,
                verticalMerge = verticalMergeValue,
                borders = bordersValue,
                shading = shadingValue,
                margins = marginsValue,
                textDirection = textDirectionValue,
                verticalAlign = verticalAlignValue,
            )
        } else {
            null
        }
        return TableCell(children = effectiveChildren, properties = props)
    }
}


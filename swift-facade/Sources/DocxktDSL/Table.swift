import Docxkt

/// A table that can sit inside a document body, header, or footer.
///
/// Table-wide styling is applied via SwiftUI-style modifiers:
///
/// ```swift
/// Table(width: .pct(5000)) {
///     Row { Cell { Paragraph { Text("hi") } } }
/// }
/// .borders(.all(BorderSide(style: .single, size: 4, color: "auto")))
/// .shading(.solidFill("EEEEEE"))
/// .cellMargins(CellMargins(top: 100, left: 120, bottom: 100, right: 120))
/// ```
public struct Table {
    internal let rows: [Row]
    internal let width: Width?
    internal let columnWidths: [Int]?
    internal var borders: Borders? = nil
    internal var shading: Shading? = nil
    internal var cellMargins: CellMargins? = nil

    /// Default table init — width inherits the underlying Kotlin DSL
    /// upstream-parity default (`tblW type=auto, w=100`), which renders
    /// poorly in Pages / LibreOffice. Real-world usage almost always
    /// wants `Table(width: .pct(5000), …)` or explicit `columnWidths`.
    public init(@TableBuilder content: () -> [Row]) {
        self.rows = content()
        self.width = nil
        self.columnWidths = nil
    }

    /// Table with explicit width and / or per-column widths.
    ///
    /// ```swift
    /// Table(width: .pct(5000), columnWidths: [3000, 3000, 3000]) {
    ///     Row { Cell { Paragraph { Text("col1") } } … }
    /// }
    /// ```
    ///
    /// `width` controls the overall table size:
    /// - `.pct(5000)` — 100% of containing column (5000 = 100% in
    ///   OOXML's fiftieths-of-percent unit)
    /// - `.dxa(twips)` — fixed absolute width in twips
    /// - `.auto()` — sizes to content (poor cross-renderer support)
    ///
    /// `columnWidths` are per-column widths in twips (1/20 pt).
    /// Total typically equals page-content width — A4 with 1" margins
    /// is `(8.27 - 2) * 1440 = 9035` twips.
    public init(
        width: Width? = nil,
        columnWidths: [Int]? = nil,
        @TableBuilder content: () -> [Row],
    ) {
        self.rows = content()
        self.width = width
        self.columnWidths = columnWidths
    }

    /// Width unit for `<w:tblW>`.
    public enum Width {
        case auto(_ size: Int = 0)
        case dxa(_ twips: Int)
        case pct(_ fiftiethsOfPercent: Int)
        case nilType
    }

    /// Set `<w:tblBorders>`. Sides left unset in the [Borders] block
    /// fall through to upstream defaults (single, size 4, auto).
    public func borders(_ value: Borders) -> Table {
        var copy = self; copy.borders = value; return copy
    }

    /// Set `<w:shd>` (table-level shading).
    public func shading(_ value: Shading) -> Table {
        var copy = self; copy.shading = value; return copy
    }

    /// Set `<w:tblCellMar>` (default margins for cells without explicit
    /// margins of their own). Twips.
    public func cellMargins(_ value: CellMargins) -> Table {
        var copy = self; copy.cellMargins = value; return copy
    }

    internal func applyToTable(_ tableScope: KotlinTableScope) {
        if let width {
            tableScope.width(value: width.kotlin)
        }
        if let columnWidths {
            tableScope.columnWidths(twips: columnWidths.toKotlinIntArray())
        }
        if let borders {
            tableScope.borders { sides in borders.apply(to: sides) }
        }
        if let shading {
            tableScope.shading(value: shading.kotlin)
        }
        if let cellMargins {
            tableScope.cellMargins(
                top: cellMargins.top.map { KotlinInt(int: Int32($0)) },
                left: cellMargins.left.map { KotlinInt(int: Int32($0)) },
                bottom: cellMargins.bottom.map { KotlinInt(int: Int32($0)) },
                right: cellMargins.right.map { KotlinInt(int: Int32($0)) },
            )
        }
        for row in rows {
            tableScope.row(configure: row.applyToRow)
        }
    }
}

extension Table.Width {
    fileprivate var kotlin: Docxkt.TableWidth {
        switch self {
        case .auto(let size):
            return Docxkt.TableWidth.companion.auto(size: Int32(size))
        case .dxa(let twips):
            return Docxkt.TableWidth.companion.dxa(twips: Int32(twips))
        case .pct(let pct):
            return Docxkt.TableWidth.companion.pct(fiftiethsOfPercent: Int32(pct))
        case .nilType:
            return Docxkt.TableWidth.companion.nilType()
        }
    }
}

extension Array where Element == Int {
    fileprivate func toKotlinIntArray() -> KotlinIntArray {
        let array = KotlinIntArray(size: Int32(count))
        for (i, v) in enumerated() {
            array.set(index: Int32(i), value: Int32(v))
        }
        return array
    }
}

/// A row inside a table.
public struct Row {
    internal let cells: [Cell]

    public init(@RowBuilder content: () -> [Cell]) {
        self.cells = content()
    }

    internal func applyToRow(_ rowScope: KotlinTableRowScope) {
        for cell in cells {
            rowScope.cell(configure: cell.applyToCell)
        }
    }
}

/// A cell inside a row. Cells contain paragraphs.
///
/// Cell-level styling is applied via SwiftUI-style modifiers:
///
/// ```swift
/// Cell { Paragraph { Text("Header A").bold() } }
///     .verticalAlign(.center)
///     .shading(.solidFill("E7E6E6"))
///     .gridSpan(2)              // colspan=2
///     .borders(Borders(bottom: BorderSide(style: .single, size: 8)))
/// ```
public struct Cell {
    internal let paragraphs: [Paragraph]
    internal var borders: Borders? = nil
    internal var shading: Shading? = nil
    internal var margins: CellMargins? = nil
    internal var verticalAlign: CellVerticalAlignment? = nil
    internal var gridSpan: Int? = nil
    internal var verticalMerge: CellVerticalMerge? = nil

    public init(@CellBuilder content: () -> [Paragraph]) {
        self.paragraphs = content()
    }

    /// Set `<w:tcBorders>`. Only sides explicitly set in the [Borders]
    /// block emit on the wire (cell-level variant — unlike table-level
    /// which fills in upstream defaults).
    public func borders(_ value: Borders) -> Cell {
        var copy = self; copy.borders = value; return copy
    }

    /// Set `<w:shd>` (cell-level shading).
    public func shading(_ value: Shading) -> Cell {
        var copy = self; copy.shading = value; return copy
    }

    /// Set `<w:tcMar>` (cell margins; overrides table-default cell margins).
    public func margins(_ value: CellMargins) -> Cell {
        var copy = self; copy.margins = value; return copy
    }

    /// Set vertical alignment of the cell content (`<w:vAlign>`).
    public func verticalAlign(_ value: CellVerticalAlignment) -> Cell {
        var copy = self; copy.verticalAlign = value; return copy
    }

    /// Merge across [columns] grid columns (`<w:gridSpan>`). Equivalent
    /// to HTML's `colspan`. Must be ≥ 1.
    public func gridSpan(_ columns: Int) -> Cell {
        var copy = self; copy.gridSpan = columns; return copy
    }

    /// Mark this cell as the start (`.restart`) or continuation
    /// (`.continue`) of a vertical merge across rows (`<w:vMerge>`).
    /// Equivalent to HTML's `rowspan` (the start cell carries content;
    /// continue cells should be empty `Cell { }`).
    public func verticalMerge(_ value: CellVerticalMerge) -> Cell {
        var copy = self; copy.verticalMerge = value; return copy
    }

    internal func applyToCell(_ cellScope: KotlinTableCellScope) {
        if let gridSpan {
            cellScope.gridSpan(columns: Int32(gridSpan))
        }
        if let verticalMerge {
            cellScope.verticalMerge(value: verticalMerge.kotlin)
        }
        if let verticalAlign {
            cellScope.verticalAlign(value: verticalAlign.kotlin)
        }
        if let borders {
            cellScope.borders { sides in borders.apply(to: sides) }
        }
        if let shading {
            cellScope.shading(value: shading.kotlin)
        }
        if let margins {
            cellScope.margins(
                top: margins.top.map { KotlinInt(int: Int32($0)) },
                left: margins.left.map { KotlinInt(int: Int32($0)) },
                bottom: margins.bottom.map { KotlinInt(int: Int32($0)) },
                right: margins.right.map { KotlinInt(int: Int32($0)) },
            )
        }
        for paragraph in paragraphs {
            cellScope.paragraph(configure: paragraph.applyToParagraph)
        }
    }
}

@resultBuilder
public enum TableBuilder {
    public static func buildBlock(_ components: Row...) -> [Row] { components }
    public static func buildOptional(_ component: [Row]?) -> [Row] { component ?? [] }
    public static func buildEither(first component: [Row]) -> [Row] { component }
    public static func buildEither(second component: [Row]) -> [Row] { component }
    public static func buildArray(_ components: [[Row]]) -> [Row] { components.flatMap { $0 } }
}

@resultBuilder
public enum RowBuilder {
    public static func buildBlock(_ components: Cell...) -> [Cell] { components }
    public static func buildOptional(_ component: [Cell]?) -> [Cell] { component ?? [] }
    public static func buildEither(first component: [Cell]) -> [Cell] { component }
    public static func buildEither(second component: [Cell]) -> [Cell] { component }
    public static func buildArray(_ components: [[Cell]]) -> [Cell] { components.flatMap { $0 } }
}

@resultBuilder
public enum CellBuilder {
    public static func buildBlock(_ components: Paragraph...) -> [Paragraph] { components }
    public static func buildOptional(_ component: [Paragraph]?) -> [Paragraph] { component ?? [] }
    public static func buildEither(first component: [Paragraph]) -> [Paragraph] { component }
    public static func buildEither(second component: [Paragraph]) -> [Paragraph] { component }
    public static func buildArray(_ components: [[Paragraph]]) -> [Paragraph] { components.flatMap { $0 } }
}

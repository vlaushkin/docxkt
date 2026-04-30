import Docxkt

/// A table that can sit inside a document body, header, or footer.
public struct Table {
    internal let rows: [Row]
    internal let width: Width?
    internal let columnWidths: [Int]?

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

    internal func applyToTable(_ tableScope: KotlinTableScope) {
        if let width {
            tableScope.width(value: width.kotlin)
        }
        if let columnWidths {
            tableScope.columnWidths(twips: columnWidths.toKotlinIntArray())
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
public struct Cell {
    internal let paragraphs: [Paragraph]

    public init(@CellBuilder content: () -> [Paragraph]) {
        self.paragraphs = content()
    }

    internal func applyToCell(_ cellScope: KotlinTableCellScope) {
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

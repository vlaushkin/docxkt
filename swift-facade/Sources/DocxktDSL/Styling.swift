import Docxkt

// MARK: - Borders

/// Style of a `<w:bdr>` / `<w:tcBdr>` / `<w:pBdr>` / `<w:pgBorders>` side.
public enum BorderStyle {
    case single, double, triple
    case dotted, dashed, dotDash, dotDotDash, dashSmallGap, dashDotStroked
    case thick, thickThinLargeGap, thickThinMediumGap, thickThinSmallGap
    case thinThickLargeGap, thinThickMediumGap, thinThickSmallGap
    case thinThickThinLargeGap, thinThickThinMediumGap, thinThickThinSmallGap
    case wave, doubleWave
    case inset, outset
    case threeDEmboss, threeDEngrave
    case nilStyle, none

    internal var kotlin: Docxkt.BorderStyle {
        switch self {
        case .single: return .single
        case .double: return .double_
        case .triple: return .triple
        case .dotted: return .dotted
        case .dashed: return .dashed
        case .dotDash: return .dotDash
        case .dotDotDash: return .dotDotDash
        case .dashSmallGap: return .dashSmallGap
        case .dashDotStroked: return .dashDotStroked
        case .thick: return .thick
        case .thickThinLargeGap: return .thickThinLargeGap
        case .thickThinMediumGap: return .thickThinMediumGap
        case .thickThinSmallGap: return .thickThinSmallGap
        case .thinThickLargeGap: return .thinThickLargeGap
        case .thinThickMediumGap: return .thinThickMediumGap
        case .thinThickSmallGap: return .thinThickSmallGap
        case .thinThickThinLargeGap: return .thinThickThinLargeGap
        case .thinThickThinMediumGap: return .thinThickThinMediumGap
        case .thinThickThinSmallGap: return .thinThickThinSmallGap
        case .wave: return .wave
        case .doubleWave: return .doubleWave
        case .inset: return .inset
        case .outset: return .outset
        case .threeDEmboss: return .threeDEmboss
        case .threeDEngrave: return .threeDEngrave
        case .nilStyle: return .nilStyle
        case .none: return .none
        }
    }
}

/// One side of a borders block (`<w:top>`, `<w:left>`, etc.).
///
/// `size` is in eighths of a point (`4` ≈ 0.5pt, the OOXML default).
/// `space` is in points (typical: 0–24). `color` is hex RGB or `"auto"`.
public struct BorderSide {
    public var style: BorderStyle
    public var size: Int?
    public var color: String?
    public var space: Int?

    public init(
        style: BorderStyle,
        size: Int? = nil,
        color: String? = nil,
        space: Int? = nil,
    ) {
        self.style = style
        self.size = size
        self.color = color
        self.space = space
    }

    internal var kotlin: Docxkt.BorderSide {
        Docxkt.BorderSide(
            style: style.kotlin,
            size: size.map { KotlinInt(int: Int32($0)) },
            color: color,
            space: space.map { KotlinInt(int: Int32($0)) },
        )
    }
}

/// Borders block applied to a paragraph, table, or cell. Set only the
/// sides you want emitted; the rest stay at upstream defaults (table-
/// level fills in upstream defaults; paragraph / cell emit only set
/// sides).
public struct Borders {
    public var top: BorderSide?
    public var bottom: BorderSide?
    public var left: BorderSide?
    public var right: BorderSide?
    public var insideHorizontal: BorderSide?
    public var insideVertical: BorderSide?
    public var between: BorderSide?
    public var start: BorderSide?
    public var end: BorderSide?

    public init(
        top: BorderSide? = nil,
        bottom: BorderSide? = nil,
        left: BorderSide? = nil,
        right: BorderSide? = nil,
        insideHorizontal: BorderSide? = nil,
        insideVertical: BorderSide? = nil,
        between: BorderSide? = nil,
        start: BorderSide? = nil,
        end: BorderSide? = nil,
    ) {
        self.top = top
        self.bottom = bottom
        self.left = left
        self.right = right
        self.insideHorizontal = insideHorizontal
        self.insideVertical = insideVertical
        self.between = between
        self.start = start
        self.end = end
    }

    /// Convenience: same `BorderSide` on all four cardinal sides.
    public static func all(_ side: BorderSide) -> Borders {
        Borders(top: side, bottom: side, left: side, right: side)
    }

    internal func apply(to scope: BorderSidesScope) {
        if let top { scope.top = top.kotlin }
        if let bottom { scope.bottom = bottom.kotlin }
        if let left { scope.left = left.kotlin }
        if let right { scope.right = right.kotlin }
        if let insideHorizontal { scope.insideHorizontal = insideHorizontal.kotlin }
        if let insideVertical { scope.insideVertical = insideVertical.kotlin }
        if let between { scope.between = between.kotlin }
        if let start { scope.start = start.kotlin }
        if let end { scope.end = end.kotlin }
    }
}

/// Page borders (`<w:pgBorders>`). Section-level. Only top/left/bottom/right —
/// page borders have no `between` / `inside*`. Adds three top-level
/// attributes: `display`, `offsetFrom`, `zOrder`.
public struct PageBorders {
    public var top: BorderSide?
    public var left: BorderSide?
    public var bottom: BorderSide?
    public var right: BorderSide?
    public var display: PageBorderDisplay?
    public var offsetFrom: PageBorderOffsetFrom?
    public var zOrder: PageBorderZOrder?

    public init(
        top: BorderSide? = nil,
        left: BorderSide? = nil,
        bottom: BorderSide? = nil,
        right: BorderSide? = nil,
        display: PageBorderDisplay? = nil,
        offsetFrom: PageBorderOffsetFrom? = nil,
        zOrder: PageBorderZOrder? = nil,
    ) {
        self.top = top
        self.left = left
        self.bottom = bottom
        self.right = right
        self.display = display
        self.offsetFrom = offsetFrom
        self.zOrder = zOrder
    }

    public static func all(
        _ side: BorderSide,
        display: PageBorderDisplay? = nil,
        offsetFrom: PageBorderOffsetFrom? = nil,
        zOrder: PageBorderZOrder? = nil,
    ) -> PageBorders {
        PageBorders(top: side, left: side, bottom: side, right: side,
                    display: display, offsetFrom: offsetFrom, zOrder: zOrder)
    }

    internal func apply(to scope: PageBordersScope) {
        if let top { scope.top = top.kotlin }
        if let left { scope.left = left.kotlin }
        if let bottom { scope.bottom = bottom.kotlin }
        if let right { scope.right = right.kotlin }
        if let display { scope.display = display.kotlin }
        if let offsetFrom { scope.offsetFrom = offsetFrom.kotlin }
        if let zOrder { scope.zOrder = zOrder.kotlin }
    }
}

public enum PageBorderDisplay {
    case allPages, firstPage, notFirstPage
    internal var kotlin: Docxkt.PageBorderDisplay {
        switch self {
        case .allPages: return .allPages
        case .firstPage: return .firstPage
        case .notFirstPage: return .notFirstPage
        }
    }
}

public enum PageBorderOffsetFrom {
    case page, text
    internal var kotlin: Docxkt.PageBorderOffsetFrom {
        switch self {
        case .page: return .page
        case .text: return .text
        }
    }
}

public enum PageBorderZOrder {
    case front, back
    internal var kotlin: Docxkt.PageBorderZOrder {
        switch self {
        case .front: return .front
        case .back: return .back
        }
    }
}

// MARK: - Spacing & Indentation

/// Paragraph spacing (`<w:spacing>`). All values in twips.
///
/// `lineRule` interprets `line`: `.auto` (240ths-per-line — Word's
/// default), `.atLeast`, `.exactly`.
public struct Spacing {
    public var before: Int?
    public var after: Int?
    public var line: Int?
    public var lineRule: LineRule?
    public var beforeAutoSpacing: Bool?
    public var afterAutoSpacing: Bool?

    public init(
        before: Int? = nil,
        after: Int? = nil,
        line: Int? = nil,
        lineRule: LineRule? = nil,
        beforeAutoSpacing: Bool? = nil,
        afterAutoSpacing: Bool? = nil,
    ) {
        self.before = before
        self.after = after
        self.line = line
        self.lineRule = lineRule
        self.beforeAutoSpacing = beforeAutoSpacing
        self.afterAutoSpacing = afterAutoSpacing
    }
}

public enum LineRule {
    case auto, atLeast, exactly
    internal var kotlin: Docxkt.LineRule {
        switch self {
        case .auto: return .auto_
        case .atLeast: return .atLeast
        case .exactly: return .exactly
        }
    }
}

/// Paragraph indentation (`<w:ind>`). All values in twips. `start` /
/// `end` are logical (RTL-aware); `left` / `right` are physical.
public struct Indentation {
    public var start: Int?
    public var end: Int?
    public var left: Int?
    public var right: Int?
    public var hanging: Int?
    public var firstLine: Int?

    public init(
        start: Int? = nil,
        end: Int? = nil,
        left: Int? = nil,
        right: Int? = nil,
        hanging: Int? = nil,
        firstLine: Int? = nil,
    ) {
        self.start = start
        self.end = end
        self.left = left
        self.right = right
        self.hanging = hanging
        self.firstLine = firstLine
    }
}

// MARK: - Cell margins, shading

/// Cell-level margins (`<w:tcMar>` or table-default `<w:tblCellMar>`).
/// Twips. `nil` side is omitted from the wire.
public struct CellMargins {
    public var top: Int?
    public var left: Int?
    public var bottom: Int?
    public var right: Int?

    public init(top: Int? = nil, left: Int? = nil, bottom: Int? = nil, right: Int? = nil) {
        self.top = top
        self.left = left
        self.bottom = bottom
        self.right = right
    }
}

/// `<w:shd>` shading. `pattern` is required; `color` and `fill` are
/// hex RGB or `"auto"`.
public struct Shading {
    public var pattern: ShadingPattern
    public var color: String?
    public var fill: String?

    public init(pattern: ShadingPattern, color: String? = nil, fill: String? = nil) {
        self.pattern = pattern
        self.color = color
        self.fill = fill
    }

    /// Solid fill — `pattern = .clear, fill = <hex>`. Most common
    /// background-color use case.
    public static func solidFill(_ hex: String) -> Shading {
        Shading(pattern: .clear, fill: hex)
    }
}

public enum ShadingPattern {
    case clear, solid, nilPattern
    case horizontalStripe, verticalStripe, reverseDiagStripe, diagStripe
    case horizontalCross, diagCross
    case thinHorizontalStripe, thinVerticalStripe
    case thinReverseDiagStripe, thinDiagStripe
    case thinHorizontalCross, thinDiagCross
    case pct5, pct10, pct12, pct15, pct20, pct25, pct30, pct35, pct37, pct40
    case pct45, pct50, pct55, pct60, pct62, pct65, pct70, pct75, pct80
    case pct85, pct87, pct90, pct95

    internal var kotlin: Docxkt.ShadingPattern {
        switch self {
        case .clear: return .clear
        case .solid: return .solid
        case .nilPattern: return .nilPattern
        case .horizontalStripe: return .horizontalStripe
        case .verticalStripe: return .verticalStripe
        case .reverseDiagStripe: return .reverseDiagStripe
        case .diagStripe: return .diagStripe
        case .horizontalCross: return .horizontalCross
        case .diagCross: return .diagCross
        case .thinHorizontalStripe: return .thinHorizontalStripe
        case .thinVerticalStripe: return .thinVerticalStripe
        case .thinReverseDiagStripe: return .thinReverseDiagStripe
        case .thinDiagStripe: return .thinDiagStripe
        case .thinHorizontalCross: return .thinHorizontalCross
        case .thinDiagCross: return .thinDiagCross
        case .pct5: return .pct5
        case .pct10: return .pct10
        case .pct12: return .pct12
        case .pct15: return .pct15
        case .pct20: return .pct20
        case .pct25: return .pct25
        case .pct30: return .pct30
        case .pct35: return .pct35
        case .pct37: return .pct37
        case .pct40: return .pct40
        case .pct45: return .pct45
        case .pct50: return .pct50
        case .pct55: return .pct55
        case .pct60: return .pct60
        case .pct62: return .pct62
        case .pct65: return .pct65
        case .pct70: return .pct70
        case .pct75: return .pct75
        case .pct80: return .pct80
        case .pct85: return .pct85
        case .pct87: return .pct87
        case .pct90: return .pct90
        case .pct95: return .pct95
        }
    }

    internal func toShadingKotlin(color: String?, fill: String?) -> Docxkt.Shading {
        Docxkt.Shading(pattern: self.kotlin, color: color, fill: fill)
    }
}

extension Shading {
    internal var kotlin: Docxkt.Shading {
        Docxkt.Shading(pattern: pattern.kotlin, color: color, fill: fill)
    }
}

// MARK: - Alignment, vertical alignment, vertical merge, highlight, line numbering

public enum Alignment {
    case left, center, right, justified, start, end
    case both, distribute, mediumKashida, highKashida, lowKashida, thaiDistribute, numTab
    internal var kotlin: AlignmentType {
        switch self {
        case .left: return .left
        case .center: return .center
        case .right: return .right
        case .justified: return .justified
        case .start: return .start
        case .end: return .end
        case .both: return .both
        case .distribute: return .distribute
        case .mediumKashida: return .mediumKashida
        case .highKashida: return .highKashida
        case .lowKashida: return .lowKashida
        case .thaiDistribute: return .thaiDistribute
        case .numTab: return .numTab
        }
    }
}

public enum CellVerticalAlignment {
    case top, center, bottom
    internal var kotlin: VerticalAlignment {
        switch self {
        case .top: return .top
        case .center: return .center
        case .bottom: return .bottom
        }
    }
}

public enum CellVerticalMerge {
    case restart, `continue`
    internal var kotlin: VerticalMerge {
        switch self {
        case .restart: return .restart
        case .continue: return .continue_
        }
    }
}

public enum HighlightColor {
    case black, blue, cyan, darkBlue, darkCyan, darkGray, darkGreen, darkMagenta
    case darkRed, darkYellow, green, lightGray, magenta, red, white, yellow, none
    internal var kotlin: Docxkt.HighlightColor {
        switch self {
        case .black: return .black
        case .blue: return .blue
        case .cyan: return .cyan
        case .darkBlue: return .darkBlue
        case .darkCyan: return .darkCyan
        case .darkGray: return .darkGray
        case .darkGreen: return .darkGreen
        case .darkMagenta: return .darkMagenta
        case .darkRed: return .darkRed
        case .darkYellow: return .darkYellow
        case .green: return .green
        case .lightGray: return .lightGray
        case .magenta: return .magenta
        case .red: return .red
        case .white: return .white
        case .yellow: return .yellow
        case .none: return .none
        }
    }
}

public enum LineNumberRestartKind {
    case continuous, newPage, newSection
    internal var kotlin: LineNumberRestart {
        switch self {
        case .continuous: return .continuous
        case .newPage: return .theNewPage
        case .newSection: return .theNewSection
        }
    }
}


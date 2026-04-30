import Docxkt

/// A paragraph that can sit inside a document body, header, footer, or
/// table cell.
///
/// Layout modifiers (`alignment`, `spacing`, `indent`, `borders`) can be
/// chained SwiftUI-style after the body builder:
///
/// ```swift
/// Paragraph { Text("Centered, double-spaced") }
///     .alignment(.center)
///     .spacing(Spacing(line: 480, lineRule: .auto))
/// ```
public struct Paragraph {
    internal let runs: [Run]
    internal let styleReference: String?
    internal let numbering: NumberingReference?
    internal var alignment: Alignment? = nil
    internal var spacing: Spacing? = nil
    internal var indentation: Indentation? = nil
    internal var borders: Borders? = nil

    public init(@ParagraphBuilder content: () -> [Run]) {
        self.runs = content()
        self.styleReference = nil
        self.numbering = nil
    }

    /// Paragraph that references a previously declared `ParagraphStyle`.
    public init(style: String, @ParagraphBuilder content: () -> [Run]) {
        self.runs = content()
        self.styleReference = style
        self.numbering = nil
    }

    /// Paragraph that participates in a list, referencing a
    /// `ListTemplate` declared earlier in the document.
    public init(numbering: NumberingReference, @ParagraphBuilder content: () -> [Run]) {
        self.runs = content()
        self.styleReference = nil
        self.numbering = numbering
    }

    /// Set horizontal alignment (`<w:jc>`).
    public func alignment(_ value: Alignment) -> Paragraph {
        var copy = self; copy.alignment = value; return copy
    }

    /// Set inter-paragraph spacing (`<w:spacing>`). Pass a `Spacing`
    /// with the fields you want emitted; unset fields are omitted.
    public func spacing(_ value: Spacing) -> Paragraph {
        var copy = self; copy.spacing = value; return copy
    }

    /// Convenience overload — twips for before / after / line plus an
    /// optional `lineRule`. Equivalent to `.spacing(Spacing(...))`.
    public func spacing(
        before: Int? = nil,
        after: Int? = nil,
        line: Int? = nil,
        lineRule: LineRule? = nil,
    ) -> Paragraph {
        spacing(Spacing(before: before, after: after, line: line, lineRule: lineRule))
    }

    /// Set paragraph indentation (`<w:ind>`). Twips. `start` / `end`
    /// are logical (RTL-aware); `left` / `right` are physical.
    public func indent(_ value: Indentation) -> Paragraph {
        var copy = self; copy.indentation = value; return copy
    }

    /// Convenience overload — equivalent to `.indent(Indentation(...))`.
    public func indent(
        start: Int? = nil,
        end: Int? = nil,
        left: Int? = nil,
        right: Int? = nil,
        hanging: Int? = nil,
        firstLine: Int? = nil,
    ) -> Paragraph {
        indent(Indentation(
            start: start, end: end, left: left, right: right,
            hanging: hanging, firstLine: firstLine,
        ))
    }

    /// Set paragraph borders (`<w:pBdr>`). Use `Borders.all(_)` for the
    /// same side everywhere, or build a `Borders(top:..., bottom:...)`
    /// directly. The `between` side applies between consecutive
    /// same-style paragraphs.
    public func borders(_ value: Borders) -> Paragraph {
        var copy = self; copy.borders = value; return copy
    }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        if let styleReference {
            paragraphScope.styleReference = styleReference
        }
        if let alignment {
            paragraphScope.alignment = alignment.kotlin
        }
        if let spacing {
            paragraphScope.spacing(
                before: spacing.before.map { KotlinInt(int: Int32($0)) },
                after: spacing.after.map { KotlinInt(int: Int32($0)) },
                line: spacing.line.map { KotlinInt(int: Int32($0)) },
                lineRule: spacing.lineRule?.kotlin,
                beforeAutoSpacing: spacing.beforeAutoSpacing.map { KotlinBoolean(value: $0) },
                afterAutoSpacing: spacing.afterAutoSpacing.map { KotlinBoolean(value: $0) },
            )
        }
        if let indentation {
            paragraphScope.indent(
                start: indentation.start.map { KotlinInt(int: Int32($0)) },
                end: indentation.end.map { KotlinInt(int: Int32($0)) },
                left: indentation.left.map { KotlinInt(int: Int32($0)) },
                right: indentation.right.map { KotlinInt(int: Int32($0)) },
                hanging: indentation.hanging.map { KotlinInt(int: Int32($0)) },
                firstLine: indentation.firstLine.map { KotlinInt(int: Int32($0)) },
            )
        }
        if let borders {
            paragraphScope.borders { sides in
                borders.apply(to: sides)
            }
        }
        if let numbering {
            paragraphScope.numbering(
                reference: numbering.listTemplateId,
                level: Int32(numbering.level),
                instance: Int32(numbering.instance),
            )
        }
        for run in runs {
            run.apply(paragraphScope)
        }
    }
}

/// One inline run inside a paragraph. `Text(...)` is the default factory;
/// further inline kinds (hyperlinks, drawings) reach the user through the
/// raw KMP→Swift bridging layer.
public struct Run {
    internal let apply: (KotlinParagraphScope) -> Void
    internal init(_ apply: @escaping (KotlinParagraphScope) -> Void) {
        self.apply = apply
    }
}

/// Plain text run with optional inline formatting.
///
/// ```swift
/// Text("plain")
/// Text("bold").bold()
/// Text("italic + underlined").italic().underline()
/// Text("colored").color("FF0000").size(28)
/// Text("highlighted").highlight(.yellow)
/// Text("strikethrough").strike()
/// Text("E=mc").superScript()
/// ```
public struct Text {
    private let value: String
    private var isBold: Bool = false
    private var isItalic: Bool = false
    private var isUnderline: Bool = false
    private var isStrike: Bool = false
    private var isDoubleStrike: Bool = false
    private var isSuperScript: Bool = false
    private var isSubScript: Bool = false
    private var styleReference: String? = nil
    private var color: String? = nil
    private var size: Int? = nil
    private var fontName: String? = nil
    private var highlight: HighlightColor? = nil

    public init(_ value: String) { self.value = value }

    public func bold() -> Text { var copy = self; copy.isBold = true; return copy }
    public func italic() -> Text { var copy = self; copy.isItalic = true; return copy }
    public func underline() -> Text { var copy = self; copy.isUnderline = true; return copy }
    public func strike() -> Text { var copy = self; copy.isStrike = true; return copy }
    public func doubleStrike() -> Text { var copy = self; copy.isDoubleStrike = true; return copy }
    public func superScript() -> Text { var copy = self; copy.isSuperScript = true; return copy }
    public func subScript() -> Text { var copy = self; copy.isSubScript = true; return copy }

    /// Run color. Hex RGB (`"FF0000"`) or the literal `"auto"`.
    public func color(_ hex: String) -> Text {
        var copy = self; copy.color = hex; return copy
    }

    /// Font size in OOXML half-points. `24` = 12pt, `28` = 14pt.
    public func size(_ halfPoints: Int) -> Text {
        var copy = self; copy.size = halfPoints; return copy
    }

    /// Font family name (applied to all four script ranges — ascii,
    /// hAnsi, cs, eastAsia — matching upstream's `createRunFonts(name)`).
    public func font(_ name: String) -> Text {
        var copy = self; copy.fontName = name; return copy
    }

    /// Highlight color (`<w:highlight>`). The OOXML standard limits
    /// highlight colors to a fixed palette — see `HighlightColor`.
    public func highlight(_ color: HighlightColor) -> Text {
        var copy = self; copy.highlight = color; return copy
    }

    /// Reference a previously declared `CharacterStyle`. Composes with
    /// other modifiers — direct modifiers override the style's
    /// properties for this run only.
    public func styled(_ characterStyleId: String) -> Text {
        var copy = self; copy.styleReference = characterStyleId; return copy
    }

    private func configureRun(_ runScope: KotlinRunScope) {
        if let styleReference { runScope.styleReference = styleReference }
        if isBold { runScope.bold = KotlinBoolean(value: true) }
        if isItalic { runScope.italics = KotlinBoolean(value: true) }
        if isUnderline { runScope.underline(type: UnderlineType.single, color: nil) }
        if isStrike { runScope.strike = KotlinBoolean(value: true) }
        if isDoubleStrike { runScope.doubleStrike = KotlinBoolean(value: true) }
        if isSuperScript { runScope.superScript = KotlinBoolean(value: true) }
        if isSubScript { runScope.subScript = KotlinBoolean(value: true) }
        if let color { runScope.color = color }
        if let size { runScope.size = KotlinInt(int: Int32(size)) }
        if let fontName { runScope.font(name: fontName, hint: nil) }
        if let highlight { runScope.highlight = highlight.kotlin }
    }

    private var hasOverrides: Bool {
        isBold || isItalic || isUnderline || isStrike || isDoubleStrike ||
            isSuperScript || isSubScript || styleReference != nil ||
            color != nil || size != nil || fontName != nil || highlight != nil
    }

    fileprivate func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        if !hasOverrides {
            paragraphScope.text(value: value)
        } else {
            paragraphScope.text(value: value, configure: configureRun)
        }
    }

    internal func applyToHyperlink(_ hyperlinkScope: KotlinHyperlinkScope) {
        if !hasOverrides {
            hyperlinkScope.text(value: value)
        } else {
            hyperlinkScope.text(value: value, configure: configureRun)
        }
    }
}

@resultBuilder
public enum ParagraphBuilder {
    public static func buildExpression(_ text: Text) -> Run {
        Run { scope in text.applyToParagraph(scope) }
    }

    public static func buildExpression(_ link: Link) -> Run {
        Run { scope in link.applyToParagraph(scope) }
    }

    public static func buildExpression(_ image: Image) -> Run {
        Run { scope in image.applyToParagraph(scope) }
    }

    public static func buildExpression(_ ref: FootnoteReference) -> Run {
        Run { scope in ref.applyToParagraph(scope) }
    }

    public static func buildExpression(_ ref: EndnoteReference) -> Run {
        Run { scope in ref.applyToParagraph(scope) }
    }

    public static func buildExpression(_ marker: CommentRangeStart) -> Run {
        Run { scope in marker.applyToParagraph(scope) }
    }

    public static func buildExpression(_ marker: CommentRangeEnd) -> Run {
        Run { scope in marker.applyToParagraph(scope) }
    }

    public static func buildExpression(_ ref: CommentReference) -> Run {
        Run { scope in ref.applyToParagraph(scope) }
    }

    public static func buildExpression(_ image: AnchorImage) -> Run {
        Run { scope in image.applyToParagraph(scope) }
    }

    public static func buildExpression(_ textbox: Textbox) -> Run {
        Run { scope in textbox.applyToParagraph(scope) }
    }

    public static func buildBlock(_ components: Run...) -> [Run] {
        components
    }

    public static func buildOptional(_ component: [Run]?) -> [Run] {
        component ?? []
    }

    public static func buildEither(first component: [Run]) -> [Run] {
        component
    }

    public static func buildEither(second component: [Run]) -> [Run] {
        component
    }

    public static func buildArray(_ components: [[Run]]) -> [Run] {
        components.flatMap { $0 }
    }
}

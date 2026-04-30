import Docxkt

/// A paragraph that can sit inside a document body, header, footer, or
/// table cell.
public struct Paragraph {
    internal let runs: [Run]
    internal let styleReference: String?
    internal let numbering: NumberingReference?

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

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        if let styleReference {
            paragraphScope.styleReference = styleReference
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
/// ```
public struct Text {
    private let value: String
    private var isBold: Bool = false
    private var isItalic: Bool = false
    private var isUnderline: Bool = false
    private var styleReference: String? = nil

    public init(_ value: String) { self.value = value }

    public func bold() -> Text { var copy = self; copy.isBold = true; return copy }
    public func italic() -> Text { var copy = self; copy.isItalic = true; return copy }
    public func underline() -> Text { var copy = self; copy.isUnderline = true; return copy }

    /// Reference a previously declared `CharacterStyle`. Composes with
    /// `.bold()` / `.italic()` / `.underline()` — direct modifiers
    /// override the style's properties for this run only.
    public func styled(_ characterStyleId: String) -> Text {
        var copy = self; copy.styleReference = characterStyleId; return copy
    }

    private func configureRun(_ runScope: KotlinRunScope) {
        if let styleReference { runScope.styleReference = styleReference }
        if isBold { runScope.bold = KotlinBoolean(value: true) }
        if isItalic { runScope.italics = KotlinBoolean(value: true) }
        if isUnderline { runScope.underline(type: UnderlineType.single, color: nil) }
    }

    private var hasOverrides: Bool { isBold || isItalic || isUnderline || styleReference != nil }

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

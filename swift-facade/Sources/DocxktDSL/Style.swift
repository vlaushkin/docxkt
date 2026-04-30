import Docxkt

/// A paragraph-level style. Define once at the top of the document and
/// reference from individual paragraphs via `Paragraph(style: "id") { … }`.
///
/// ```swift
/// Document {
///     ParagraphStyle(id: "Heading1", name: "Heading 1", basedOn: "Normal",
///                    bold: true, size: 32, color: "1F3864")
///     Paragraph(style: "Heading1") { Text("Q1 Report") }
/// }
/// ```
public struct ParagraphStyle {
    public var id: String
    public var name: String?
    public var basedOn: String?
    public var next: String?
    public var bold: Bool?
    public var italic: Bool?
    public var underline: Bool?
    public var size: Int?
    public var color: String?
    public var fontFamily: String?

    public init(
        id: String,
        name: String? = nil,
        basedOn: String? = nil,
        next: String? = nil,
        bold: Bool? = nil,
        italic: Bool? = nil,
        underline: Bool? = nil,
        size: Int? = nil,
        color: String? = nil,
        fontFamily: String? = nil,
    ) {
        self.id = id
        self.name = name
        self.basedOn = basedOn
        self.next = next
        self.bold = bold
        self.italic = italic
        self.underline = underline
        self.size = size
        self.color = color
        self.fontFamily = fontFamily
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        let styleId = id
        scope.paragraphStyle(id: styleId) { [self] styleScope in
            if let name { styleScope.name(value: name) }
            if let basedOn { styleScope.basedOn(value: basedOn) }
            if let next { styleScope.next(value: next) }
            styleScope.run { runScope in
                if let bold { runScope.bold = KotlinBoolean(value: bold) }
                if let italic { runScope.italics = KotlinBoolean(value: italic) }
                if let underline, underline {
                    runScope.underline(type: UnderlineType.single, color: nil)
                }
                if let size { runScope.size = KotlinInt(int: Int32(size)) }
                if let color { runScope.color = color }
                if let fontFamily { runScope.font(name: fontFamily, hint: nil) }
            }
        }
    }
}

/// A character (run) style. Reference via `Text("…").styled("id")`.
public struct CharacterStyle {
    public var id: String
    public var name: String?
    public var basedOn: String?
    public var bold: Bool?
    public var italic: Bool?
    public var size: Int?
    public var color: String?
    public var fontFamily: String?

    public init(
        id: String,
        name: String? = nil,
        basedOn: String? = nil,
        bold: Bool? = nil,
        italic: Bool? = nil,
        size: Int? = nil,
        color: String? = nil,
        fontFamily: String? = nil,
    ) {
        self.id = id
        self.name = name
        self.basedOn = basedOn
        self.bold = bold
        self.italic = italic
        self.size = size
        self.color = color
        self.fontFamily = fontFamily
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        let styleId = id
        scope.characterStyle(id: styleId) { [self] styleScope in
            if let name { styleScope.name(value: name) }
            if let basedOn { styleScope.basedOn(value: basedOn) }
            styleScope.run { runScope in
                if let bold { runScope.bold = KotlinBoolean(value: bold) }
                if let italic { runScope.italics = KotlinBoolean(value: italic) }
                if let size { runScope.size = KotlinInt(int: Int32(size)) }
                if let color { runScope.color = color }
                if let fontFamily { runScope.font(name: fontFamily, hint: nil) }
            }
        }
    }
}

import Docxkt

/// Declares a list template (bulleted or numbered) at the document level.
/// Reference it from individual paragraphs via
/// `Paragraph(numbering: NumberingReference("id", level: 0)) { … }`.
///
/// ```swift
/// Document {
///     ListTemplate(id: "MyBullets",
///         Level(0, format: .bullet, text: "•", indent: 720, hanging: 360),
///         Level(1, format: .bullet, text: "○", indent: 1440, hanging: 360),
///     )
///     Paragraph(numbering: NumberingReference("MyBullets", level: 0)) {
///         Text("First item")
///     }
/// }
/// ```
public struct ListTemplate {
    public var id: String
    public var levels: [Level]

    public init(id: String, _ levels: Level...) {
        self.id = id
        self.levels = levels
    }

    public struct Level {
        public var index: Int
        public var format: Format
        public var text: String
        public var start: Int = 1
        public var indent: Int?
        public var hanging: Int?

        public init(
            _ index: Int,
            format: Format,
            text: String,
            start: Int = 1,
            indent: Int? = nil,
            hanging: Int? = nil,
        ) {
            self.index = index
            self.format = format
            self.text = text
            self.start = start
            self.indent = indent
            self.hanging = hanging
        }
    }

    public enum Format {
        case bullet
        case decimal
        case lowerLetter
        case upperLetter
        case lowerRoman
        case upperRoman

        fileprivate var kotlin: LevelFormat {
            switch self {
            case .bullet: return .bullet
            case .decimal: return .decimal
            case .lowerLetter: return .lowerLetter
            case .upperLetter: return .upperLetter
            case .lowerRoman: return .lowerRoman
            case .upperRoman: return .upperRoman
            }
        }
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.listTemplate(reference: id) { [levels] templateScope in
            for level in levels {
                templateScope.level(
                    level: Int32(level.index),
                    format: level.format.kotlin,
                    text: level.text,
                    start: Int32(level.start),
                    justification: AlignmentType.left,
                    indentLeft: level.indent.map { KotlinInt(int: Int32($0)) },
                    indentHanging: level.hanging.map { KotlinInt(int: Int32($0)) },
                    configure: { _ in },
                )
            }
        }
    }
}

/// A reference to a previously declared list template, used in
/// `Paragraph(numbering: …)`.
public struct NumberingReference {
    public var listTemplateId: String
    public var level: Int
    public var instance: Int

    public init(_ listTemplateId: String, level: Int = 0, instance: Int = 0) {
        self.listTemplateId = listTemplateId
        self.level = level
        self.instance = instance
    }
}

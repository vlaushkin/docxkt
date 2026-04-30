import Docxkt

/// A section break — divides the document into sections with their own
/// page orientation, columns, margins, etc. Place between paragraphs /
/// tables to apply different page setup to the content that follows.
///
/// ```swift
/// Document {
///     Paragraph { Text("Portrait section") }
///     Section(orientation: .landscape, columns: 2)
///     Paragraph { Text("Landscape, two-column section") }
/// }
/// ```
public struct Section {
    /// Page orientation for the section. `nil` inherits the previous
    /// section's orientation (portrait by default).
    public var orientation: PageOrientation?

    /// Number of columns. `nil` keeps the default single column.
    public var columns: Int?

    /// Section type — page break behavior. `.nextPage` is the default.
    public var type: SectionType?

    /// Whether the first page of this section is a title page (uses the
    /// document's "first" header/footer instead of "default").
    public var hasTitlePage: Bool = false

    public init(
        orientation: PageOrientation? = nil,
        columns: Int? = nil,
        type: SectionType? = nil,
        hasTitlePage: Bool = false,
    ) {
        self.orientation = orientation
        self.columns = columns
        self.type = type
        self.hasTitlePage = hasTitlePage
    }

    public enum PageOrientation {
        case portrait
        case landscape
    }

    public enum SectionType {
        case nextPage
        case continuous
        case nextColumn
        case oddPage
        case evenPage
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.sectionBreak { sectionScope in
            if let orientation {
                switch orientation {
                case .portrait: sectionScope.a4Portrait()
                case .landscape: sectionScope.a4Landscape()
                }
            }
            if let columns {
                sectionScope.columns(
                    count: Int32(columns),
                    equalWidth: KotlinBoolean(value: true),
                    spaceTwips: nil,
                    separator: nil,
                    individual: [],
                )
            }
            if let type {
                sectionScope.type(value: type.kotlin)
            }
            if hasTitlePage {
                sectionScope.titlePage()
            }
        }
    }
}

extension Section.SectionType {
    fileprivate var kotlin: Docxkt.SectionType {
        switch self {
        case .nextPage: return .nextPage
        case .continuous: return .continuous
        case .nextColumn: return .nextColumn
        case .oddPage: return .oddPage
        case .evenPage: return .evenPage
        }
    }
}

import Docxkt

/// Document-level metadata. Sits at the top of `Document { … }`.
///
/// ```swift
/// Document {
///     Properties(
///         title: "Q1 Report",
///         creator: "Jane Doe",
///         keywords: "finance,quarterly",
///         custom: ["department": "Sales"],
///     )
///     Paragraph { Text("…") }
/// }
/// ```
public struct Properties {
    public var title: String?
    public var subject: String?
    public var creator: String?
    public var keywords: String?
    public var description: String?
    public var lastModifiedBy: String?
    public var revision: Int?

    /// ISO-8601 timestamp string. Defaults to "now" when nil.
    public var createdAt: String?
    /// ISO-8601 timestamp string. Defaults to [createdAt] when nil.
    public var modifiedAt: String?

    /// Custom (vendor) properties — appear in `docProps/custom.xml`.
    public var custom: [String: String]

    public init(
        title: String? = nil,
        subject: String? = nil,
        creator: String? = nil,
        keywords: String? = nil,
        description: String? = nil,
        lastModifiedBy: String? = nil,
        revision: Int? = nil,
        createdAt: String? = nil,
        modifiedAt: String? = nil,
        custom: [String: String] = [:],
    ) {
        self.title = title
        self.subject = subject
        self.creator = creator
        self.keywords = keywords
        self.description = description
        self.lastModifiedBy = lastModifiedBy
        self.revision = revision
        self.createdAt = createdAt
        self.modifiedAt = modifiedAt
        self.custom = custom
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.properties { propertiesScope in
            propertiesScope.title = title
            propertiesScope.subject = subject
            propertiesScope.creator = creator
            propertiesScope.keywords = keywords
            propertiesScope.description_ = description
            propertiesScope.lastModifiedBy = lastModifiedBy
            if let revision { propertiesScope.revision = KotlinInt(int: Int32(revision)) }
            propertiesScope.createdAt = createdAt
            propertiesScope.modifiedAt = modifiedAt
            for (name, value) in custom {
                propertiesScope.custom(name: name, value: value)
            }
        }
    }
}

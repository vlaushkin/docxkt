import Docxkt

// Footnote, Endnote, and Comment — annotation scopes that pair a
// document-level *definition* with one or more inline *references*
// inside paragraphs.
//
// ```swift
// Document {
//     Footnote(id: 1) { Paragraph { Text("First-cited fact: ...") } }
//     Comment(id: 1, author: "Reviewer", date: "2026-04-30T10:00:00Z") {
//         Paragraph { Text("Please rephrase.") }
//     }
//     Paragraph {
//         Text("See note")
//         FootnoteReference(id: 1)
//         Text(" for context.")
//         CommentRangeStart(id: 1)
//         Text("This phrase needs review")
//         CommentRangeEnd(id: 1)
//         CommentReference(id: 1)
//     }
// }
// ```

/// A footnote definition placed at document scope. Reference it inline
/// via `FootnoteReference(id:)` inside a `Paragraph`.
public struct Footnote {
    public var id: Int
    public var paragraphs: [Paragraph]

    public init(id: Int, @CellBuilder content: () -> [Paragraph]) {
        self.id = id
        self.paragraphs = content()
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.footnote(id: Int32(id)) { [paragraphs] footnoteScope in
            for p in paragraphs {
                footnoteScope.paragraph(configure: p.applyToParagraph)
            }
        }
    }
}

/// An endnote definition placed at document scope. Same shape as
/// `Footnote` — uses the same underlying `FootnoteScope`.
public struct Endnote {
    public var id: Int
    public var paragraphs: [Paragraph]

    public init(id: Int, @CellBuilder content: () -> [Paragraph]) {
        self.id = id
        self.paragraphs = content()
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.endnote(id: Int32(id)) { [paragraphs] footnoteScope in
            for p in paragraphs {
                footnoteScope.paragraph(configure: p.applyToParagraph)
            }
        }
    }
}

/// A comment definition placed at document scope. Reference it inline
/// via `CommentRangeStart` / `CommentRangeEnd` / `CommentReference`.
public struct Comment {
    public var id: Int
    public var author: String?
    public var initials: String?
    public var date: String
    public var paragraphs: [Paragraph]

    public init(
        id: Int,
        author: String? = nil,
        initials: String? = nil,
        date: String,
        @CellBuilder content: () -> [Paragraph],
    ) {
        self.id = id
        self.author = author
        self.initials = initials
        self.date = date
        self.paragraphs = content()
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.comment(
            id: Int32(id),
            author: author,
            initials: initials,
            date: date,
        ) { [paragraphs] commentScope in
            for p in paragraphs {
                commentScope.paragraph(configure: p.applyToParagraph)
            }
        }
    }
}

/// Inline footnote reference — emits the superscript number that ties
/// surrounding text to the corresponding `Footnote(id:)` definition.
public struct FootnoteReference {
    public var id: Int
    public init(id: Int) { self.id = id }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.footnoteReference(id: Int32(id))
    }
}

/// Inline endnote reference.
public struct EndnoteReference {
    public var id: Int
    public init(id: Int) { self.id = id }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.endnoteReference(id: Int32(id))
    }
}

/// Inline marker — start of a commented range.
public struct CommentRangeStart {
    public var id: Int
    public init(id: Int) { self.id = id }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.commentRangeStart(id: Int32(id))
    }
}

/// Inline marker — end of a commented range.
public struct CommentRangeEnd {
    public var id: Int
    public init(id: Int) { self.id = id }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.commentRangeEnd(id: Int32(id))
    }
}

/// Inline reference to a comment — emits the comment marker (typically
/// rendered as a small balloon icon by the consumer).
public struct CommentReference {
    public var id: Int
    public init(id: Int) { self.id = id }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.commentReference(id: Int32(id))
    }
}

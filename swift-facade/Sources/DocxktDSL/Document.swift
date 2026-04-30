import Docxkt
import Foundation

/// A `.docx` document built declaratively via `Document { … }`.
///
/// ```swift
/// let doc = Document {
///     Paragraph { Text("Hello, world!") }
///     Table {
///         Row {
///             Cell { Paragraph { Text("A1") } }
///             Cell { Paragraph { Text("B1") } }
///         }
///     }
/// }
/// let data = doc.toData()
/// ```
public struct Document {

    internal let kotlin: KotlinDocument

    public init(@DocumentBuilder content: () -> [DocumentBlock]) {
        let blocks = content()
        self.kotlin = DocumentKt.document { scope in
            for block in blocks {
                block.apply(scope)
            }
        }
    }

    /// Serialize the document as `.docx` ZIP bytes.
    public func toData() -> Data {
        let kotlinBytes = kotlin.toByteArray()
        let count = Int(kotlinBytes.size)
        var data = Data(count: count)
        data.withUnsafeMutableBytes { dst in
            guard let dstBase = dst.bindMemory(to: Int8.self).baseAddress else { return }
            for i in 0..<count {
                dstBase[i] = kotlinBytes.get(index: Int32(i))
            }
        }
        return data
    }

    /// Write the document to [url], overwriting any existing file.
    public func write(to url: URL) throws {
        try toData().write(to: url)
    }
}

/// Type-erased value carrying the application step for one direct child of a
/// `Document` body.
public struct DocumentBlock {
    internal let apply: (KotlinDocumentScope) -> Void
    internal init(_ apply: @escaping (KotlinDocumentScope) -> Void) {
        self.apply = apply
    }
}

@resultBuilder
public enum DocumentBuilder {
    public static func buildExpression(_ paragraph: Paragraph) -> DocumentBlock {
        DocumentBlock { scope in scope.paragraph(configure: paragraph.applyToParagraph) }
    }

    public static func buildExpression(_ table: Table) -> DocumentBlock {
        DocumentBlock { scope in scope.table(configure: table.applyToTable) }
    }

    public static func buildExpression(_ header: Header) -> DocumentBlock {
        DocumentBlock { scope in
            scope.header(type: HeaderFooterReferenceType.default_, configure: header.applyToHeader)
        }
    }

    public static func buildExpression(_ footer: Footer) -> DocumentBlock {
        DocumentBlock { scope in
            scope.footer(type: HeaderFooterReferenceType.default_, configure: footer.applyToFooter)
        }
    }

    public static func buildExpression(_ section: Section) -> DocumentBlock {
        DocumentBlock { scope in section.applyToDocument(scope) }
    }

    public static func buildExpression(_ properties: Properties) -> DocumentBlock {
        DocumentBlock { scope in properties.applyToDocument(scope) }
    }

    public static func buildExpression(_ style: ParagraphStyle) -> DocumentBlock {
        DocumentBlock { scope in style.applyToDocument(scope) }
    }

    public static func buildExpression(_ style: CharacterStyle) -> DocumentBlock {
        DocumentBlock { scope in style.applyToDocument(scope) }
    }

    public static func buildExpression(_ list: ListTemplate) -> DocumentBlock {
        DocumentBlock { scope in list.applyToDocument(scope) }
    }

    public static func buildExpression(_ footnote: Footnote) -> DocumentBlock {
        DocumentBlock { scope in footnote.applyToDocument(scope) }
    }

    public static func buildExpression(_ endnote: Endnote) -> DocumentBlock {
        DocumentBlock { scope in endnote.applyToDocument(scope) }
    }

    public static func buildExpression(_ comment: Comment) -> DocumentBlock {
        DocumentBlock { scope in comment.applyToDocument(scope) }
    }

    public static func buildExpression(_ lineNumbering: LineNumbering) -> DocumentBlock {
        DocumentBlock { scope in lineNumbering.applyToDocument(scope) }
    }

    public static func buildBlock(_ components: DocumentBlock...) -> [DocumentBlock] {
        components
    }

    public static func buildOptional(_ component: [DocumentBlock]?) -> [DocumentBlock] {
        component ?? []
    }

    public static func buildEither(first component: [DocumentBlock]) -> [DocumentBlock] {
        component
    }

    public static func buildEither(second component: [DocumentBlock]) -> [DocumentBlock] {
        component
    }

    public static func buildArray(_ components: [[DocumentBlock]]) -> [DocumentBlock] {
        components.flatMap { $0 }
    }
}

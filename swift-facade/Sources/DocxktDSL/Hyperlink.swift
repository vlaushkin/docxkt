import Docxkt

/// An external hyperlink inside a paragraph. Wraps one or more `Text`
/// runs that share the same `url` target.
///
/// ```swift
/// Paragraph {
///     Text("Visit ")
///     Link("https://example.com") {
///         Text("our site").bold()
///     }
///     Text(" for more.")
/// }
/// ```
public struct Link {
    private let url: String
    private let texts: [Text]

    public init(_ url: String, @LinkBuilder content: () -> [Text]) {
        self.url = url
        self.texts = content()
    }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.hyperlink(url: url) { [texts] hyperlinkScope in
            for text in texts {
                text.applyToHyperlink(hyperlinkScope)
            }
        }
    }
}

@resultBuilder
public enum LinkBuilder {
    public static func buildExpression(_ text: Text) -> Text { text }
    public static func buildBlock(_ components: Text...) -> [Text] { components }
    public static func buildOptional(_ component: [Text]?) -> [Text] { component ?? [] }
    public static func buildEither(first component: [Text]) -> [Text] { component }
    public static func buildEither(second component: [Text]) -> [Text] { component }
    public static func buildArray(_ components: [[Text]]) -> [Text] { components.flatMap { $0 } }
}

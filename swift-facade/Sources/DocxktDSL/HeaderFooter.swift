import Docxkt

/// A header that appears at the top of every page (default reference type).
public struct Header {
    internal let blocks: [HeaderFooterBlock]

    public init(@HeaderFooterBuilder content: () -> [HeaderFooterBlock]) {
        self.blocks = content()
    }

    internal func applyToHeader(_ scope: KotlinHeaderScope) {
        for block in blocks {
            block.applyToHeader(scope)
        }
    }
}

/// A footer that appears at the bottom of every page (default reference type).
public struct Footer {
    internal let blocks: [HeaderFooterBlock]

    public init(@HeaderFooterBuilder content: () -> [HeaderFooterBlock]) {
        self.blocks = content()
    }

    internal func applyToFooter(_ scope: KotlinFooterScope) {
        for block in blocks {
            block.applyToFooter(scope)
        }
    }
}

/// Type-erased child of a `Header` or `Footer` body. Currently supports
/// `Paragraph` and `Table`.
public struct HeaderFooterBlock {
    internal let applyToHeader: (KotlinHeaderScope) -> Void
    internal let applyToFooter: (KotlinFooterScope) -> Void

    internal init(
        applyToHeader: @escaping (KotlinHeaderScope) -> Void,
        applyToFooter: @escaping (KotlinFooterScope) -> Void,
    ) {
        self.applyToHeader = applyToHeader
        self.applyToFooter = applyToFooter
    }
}

@resultBuilder
public enum HeaderFooterBuilder {
    public static func buildExpression(_ paragraph: Paragraph) -> HeaderFooterBlock {
        HeaderFooterBlock(
            applyToHeader: { scope in scope.paragraph(configure: paragraph.applyToParagraph) },
            applyToFooter: { scope in scope.paragraph(configure: paragraph.applyToParagraph) },
        )
    }

    public static func buildExpression(_ table: Table) -> HeaderFooterBlock {
        HeaderFooterBlock(
            applyToHeader: { scope in scope.table(configure: table.applyToTable) },
            applyToFooter: { scope in scope.table(configure: table.applyToTable) },
        )
    }

    public static func buildBlock(_ components: HeaderFooterBlock...) -> [HeaderFooterBlock] {
        components
    }

    public static func buildOptional(_ component: [HeaderFooterBlock]?) -> [HeaderFooterBlock] {
        component ?? []
    }

    public static func buildEither(first component: [HeaderFooterBlock]) -> [HeaderFooterBlock] {
        component
    }

    public static func buildEither(second component: [HeaderFooterBlock]) -> [HeaderFooterBlock] {
        component
    }

    public static func buildArray(_ components: [[HeaderFooterBlock]]) -> [HeaderFooterBlock] {
        components.flatMap { $0 }
    }
}

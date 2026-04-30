import Docxkt
import Foundation

// Drawing — floating (anchored) images and textboxes. Inline images
// already covered by `Image`; this file adds the positionable variants.
//
// Positioning + wrapping enums are reused from the bridged Kotlin
// module: `import Docxkt` already pulls them in for consumer code.
// Re-exported below as type aliases for ergonomic facade use.

public typealias AnchorHorizontalRelativeFrom = HorizontalRelativeFrom
public typealias AnchorVerticalRelativeFrom = VerticalRelativeFrom
public typealias AnchorHorizontalAlign = HorizontalAlign
public typealias AnchorVerticalAlign = VerticalAlign
public typealias AnchorWrapSide = WrapSide

/// A floating image with positioning + text-wrap behaviour. Use inside
/// a `Paragraph` like an inline `Image`, but with an `AnchorConfig`
/// closure that sets `positionH` / `positionV` / `wrap*`.
///
/// ```swift
/// Paragraph {
///     AnchorImage(data: png, widthEmus: 1_905_000, heightEmus: 952_500,
///                 format: .png) { anchor in
///         anchor.positionH(relativeFrom: .column, offsetEmus: 0)
///         anchor.positionV(relativeFrom: .paragraph, offsetEmus: 0)
///         anchor.wrapSquare(side: .bothSides)
///     }
/// }
/// ```
public struct AnchorImage {
    public typealias Configure = (AnchorConfig) -> Void

    private let bytes: Data
    private let widthEmus: Int
    private let heightEmus: Int
    private let format: Image.Format
    private let description: String?
    private let configure: Configure

    public init(
        data: Data,
        widthEmus: Int,
        heightEmus: Int,
        format: Image.Format,
        description: String? = nil,
        configure: @escaping Configure,
    ) {
        self.bytes = data
        self.widthEmus = widthEmus
        self.heightEmus = heightEmus
        self.format = format
        self.description = description
        self.configure = configure
    }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        let kotlinBytes = bytes.toKotlinByteArrayInternal()
        paragraphScope.imageAnchor(
            bytes: kotlinBytes,
            widthEmus: Int32(widthEmus),
            heightEmus: Int32(heightEmus),
            format: format.kotlin,
            description: description,
        ) { [configure] anchorScope in
            configure(AnchorConfig(scope: anchorScope))
        }
    }
}

/// Wraps the bridged `KotlinAnchorScope` to expose the positioning +
/// wrap surface idiomatically inside the `AnchorImage` configure block.
public struct AnchorConfig {
    fileprivate let scope: AnchorScope

    public func positionH(relativeFrom: AnchorHorizontalRelativeFrom, offsetEmus: Int) {
        scope.positionH(relativeFrom: relativeFrom, offsetEmus: Int32(offsetEmus))
    }
    public func positionH(relativeFrom: AnchorHorizontalRelativeFrom, align: AnchorHorizontalAlign) {
        scope.positionH(relativeFrom: relativeFrom, align: align)
    }
    public func positionV(relativeFrom: AnchorVerticalRelativeFrom, offsetEmus: Int) {
        scope.positionV(relativeFrom: relativeFrom, offsetEmus: Int32(offsetEmus))
    }
    public func positionV(relativeFrom: AnchorVerticalRelativeFrom, align: AnchorVerticalAlign) {
        scope.positionV(relativeFrom: relativeFrom, align: align)
    }

    public func wrapSquare(
        side: AnchorWrapSide = .bothSides,
        marginTopEmus: Int = 0,
        marginBottomEmus: Int = 0,
        marginLeftEmus: Int = 0,
        marginRightEmus: Int = 0,
    ) {
        scope.wrapSquare(
            side: side,
            marginTopEmus: Int32(marginTopEmus),
            marginBottomEmus: Int32(marginBottomEmus),
            marginLeftEmus: Int32(marginLeftEmus),
            marginRightEmus: Int32(marginRightEmus),
        )
    }

    public func wrapTight(marginTopEmus: Int = 0, marginBottomEmus: Int = 0) {
        scope.wrapTight(marginTopEmus: Int32(marginTopEmus), marginBottomEmus: Int32(marginBottomEmus))
    }

    public func wrapTopAndBottom(marginTopEmus: Int = 0, marginBottomEmus: Int = 0) {
        scope.wrapTopAndBottom(
            marginTopEmus: Int32(marginTopEmus),
            marginBottomEmus: Int32(marginBottomEmus),
        )
    }

    public func wrapNone() { scope.wrapNone() }

    public func behindDoc(_ value: Bool) { scope.behindDoc = value }
    public func allowOverlap(_ value: Bool) { scope.allowOverlap = value }
    public func layoutInCell(_ value: Bool) { scope.layoutInCell = value }
}

/// A textbox — a `<wps:wsp>` shape carrying paragraphs in its body.
/// Inline form (sits in a paragraph like a regular run); floating
/// textboxes (anchor wrapper) are reachable via raw bridging.
///
/// ```swift
/// Paragraph {
///     Textbox(widthEmus: 1_905_000, heightEmus: 952_500) {
///         Paragraph { Text("Inside the box").bold() }
///     }
/// }
/// ```
public struct Textbox {
    public var widthEmus: Int
    public var heightEmus: Int
    public var description: String?
    public var paragraphs: [Paragraph]

    public init(
        widthEmus: Int,
        heightEmus: Int,
        description: String? = nil,
        @CellBuilder content: () -> [Paragraph],
    ) {
        self.widthEmus = widthEmus
        self.heightEmus = heightEmus
        self.description = description
        self.paragraphs = content()
    }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        paragraphScope.textbox(
            widthEmus: Int32(widthEmus),
            heightEmus: Int32(heightEmus),
            description: description,
        ) { [paragraphs] textboxScope in
            for p in paragraphs {
                textboxScope.paragraph(configure: p.applyToParagraph)
            }
        }
    }
}

extension Data {
    fileprivate func toKotlinByteArrayInternal() -> KotlinByteArray {
        let array = KotlinByteArray(size: Int32(count))
        withUnsafeBytes { (raw: UnsafeRawBufferPointer) in
            guard let base = raw.bindMemory(to: Int8.self).baseAddress else { return }
            for i in 0..<count {
                array.set(index: Int32(i), value: base[i])
            }
        }
        return array
    }
}

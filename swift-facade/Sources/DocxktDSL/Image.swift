import Docxkt
import Foundation

/// An inline image embedded in a paragraph.
///
/// ```swift
/// Paragraph {
///     Image(
///         data: try Data(contentsOf: pngURL),
///         widthEmus: 1_905_000, heightEmus: 952_500,
///         format: .png,
///     )
/// }
/// ```
///
/// EMU = English Metric Unit. 914_400 EMU = 1 inch. 9_525 EMU per pixel
/// at 96 dpi (the OOXML default). For a 200 px square image at 96 dpi
/// pass `widthEmus: 1_905_000, heightEmus: 1_905_000`.
public struct Image {
    public enum Format {
        case png, jpeg, gif, bmp

        internal var kotlin: ImageFormat {
            switch self {
            case .png: return .png
            case .jpeg: return .jpeg
            case .gif: return .gif
            case .bmp: return .bmp
            }
        }
    }

    private let bytes: Data
    private let widthEmus: Int
    private let heightEmus: Int
    private let format: Format
    private let description: String?

    public init(
        data: Data,
        widthEmus: Int,
        heightEmus: Int,
        format: Format,
        description: String? = nil,
    ) {
        self.bytes = data
        self.widthEmus = widthEmus
        self.heightEmus = heightEmus
        self.format = format
        self.description = description
    }

    internal func applyToParagraph(_ paragraphScope: KotlinParagraphScope) {
        let kotlinBytes = bytes.toKotlinByteArray()
        paragraphScope.image(
            bytes: kotlinBytes,
            widthEmus: Int32(widthEmus),
            heightEmus: Int32(heightEmus),
            format: format.kotlin,
            description: description,
        )
    }
}

extension Data {
    /// Bridge a Swift `Data` to the Kotlin `ByteArray` the docxkt
    /// runtime expects. Per-byte copy — fine for typical image sizes
    /// (a few hundred KB), avoid for tens-of-MB payloads.
    fileprivate func toKotlinByteArray() -> KotlinByteArray {
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

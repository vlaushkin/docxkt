import Docxkt

/// Document-level line numbering (`<w:lnNumType>`). Sits as a top-level
/// `Document { LineNumbering(...) }` block.
///
/// ```swift
/// Document {
///     LineNumbering(countBy: 1, start: 1, distance: 360, restart: .newPage)
///     Paragraph { Text("Numbered every line, restart per page") }
/// }
/// ```
public struct LineNumbering {
    public var countBy: Int?
    public var start: Int?
    public var distance: Int?
    public var restart: LineNumberRestartKind?

    public init(
        countBy: Int? = nil,
        start: Int? = nil,
        distance: Int? = nil,
        restart: LineNumberRestartKind? = nil,
    ) {
        self.countBy = countBy
        self.start = start
        self.distance = distance
        self.restart = restart
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.lineNumbering(
            countBy: countBy.map { KotlinInt(int: Int32($0)) },
            start: start.map { KotlinInt(int: Int32($0)) },
            distanceTwips: distance.map { KotlinInt(int: Int32($0)) },
            restart: restart?.kotlin,
        )
    }
}

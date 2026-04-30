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

    /// Page margins for this section. `nil` keeps the upstream
    /// defaults (1 inch on all sides, 0.49 inch header/footer).
    public var margins: PageMargins?

    /// `<w:pgBorders>` — page borders for this section. `nil` omits
    /// the element entirely.
    public var pageBorders: PageBorders?

    public init(
        orientation: PageOrientation? = nil,
        columns: Int? = nil,
        type: SectionType? = nil,
        hasTitlePage: Bool = false,
        margins: PageMargins? = nil,
        pageBorders: PageBorders? = nil,
    ) {
        self.orientation = orientation
        self.columns = columns
        self.type = type
        self.hasTitlePage = hasTitlePage
        self.margins = margins
        self.pageBorders = pageBorders
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

    /// `<w:pgMar>` page margins, in twips (1 inch = 1440 twips).
    ///
    /// Defaults match upstream's section-properties verbatim:
    /// `top/right/bottom/left = 1440` (1 inch),
    /// `header/footer = 708` (≈0.49 inch — upstream uses 708, not 720),
    /// `gutter = 0`.
    ///
    /// ```swift
    /// Section(margins: .inches(top: 0.5, bottom: 0.5))
    /// Section(margins: Section.PageMargins(top: 720, bottom: 720))
    /// ```
    public struct PageMargins {
        public var top: Int
        public var right: Int
        public var bottom: Int
        public var left: Int
        public var header: Int
        public var footer: Int
        public var gutter: Int

        public init(
            top: Int = 1440,
            right: Int = 1440,
            bottom: Int = 1440,
            left: Int = 1440,
            header: Int = 708,
            footer: Int = 708,
            gutter: Int = 0,
        ) {
            self.top = top
            self.right = right
            self.bottom = bottom
            self.left = left
            self.header = header
            self.footer = footer
            self.gutter = gutter
        }

        /// Build margins from inches (1 inch = 1440 twips). Header/footer
        /// default to 0.49 inch (708 twips) for byte-parity with upstream.
        public static func inches(
            top: Double = 1.0,
            right: Double = 1.0,
            bottom: Double = 1.0,
            left: Double = 1.0,
            header: Double = 0.49,
            footer: Double = 0.49,
            gutter: Double = 0,
        ) -> PageMargins {
            PageMargins(
                top: Int((top * 1440).rounded()),
                right: Int((right * 1440).rounded()),
                bottom: Int((bottom * 1440).rounded()),
                left: Int((left * 1440).rounded()),
                header: Int((header * 1440).rounded()),
                footer: Int((footer * 1440).rounded()),
                gutter: Int((gutter * 1440).rounded()),
            )
        }

        /// Build margins from centimetres (1 inch = 2.54 cm; 1 cm ≈
        /// 567 twips). Header/footer default to 1.25 cm.
        public static func cm(
            top: Double = 2.54,
            right: Double = 2.54,
            bottom: Double = 2.54,
            left: Double = 2.54,
            header: Double = 1.25,
            footer: Double = 1.25,
            gutter: Double = 0,
        ) -> PageMargins {
            let twipsPerCm = 1440.0 / 2.54
            return PageMargins(
                top: Int((top * twipsPerCm).rounded()),
                right: Int((right * twipsPerCm).rounded()),
                bottom: Int((bottom * twipsPerCm).rounded()),
                left: Int((left * twipsPerCm).rounded()),
                header: Int((header * twipsPerCm).rounded()),
                footer: Int((footer * twipsPerCm).rounded()),
                gutter: Int((gutter * twipsPerCm).rounded()),
            )
        }
    }

    internal func applyToDocument(_ scope: KotlinDocumentScope) {
        scope.sectionBreak { sectionScope in
            if let orientation {
                switch orientation {
                case .portrait: sectionScope.a4Portrait()
                case .landscape: sectionScope.a4Landscape()
                }
            }
            if let margins {
                sectionScope.margins(
                    top: KotlinInt(int: Int32(margins.top)),
                    right: KotlinInt(int: Int32(margins.right)),
                    bottom: KotlinInt(int: Int32(margins.bottom)),
                    left: KotlinInt(int: Int32(margins.left)),
                    header: KotlinInt(int: Int32(margins.header)),
                    footer: KotlinInt(int: Int32(margins.footer)),
                    gutter: KotlinInt(int: Int32(margins.gutter)),
                )
            }
            if let pageBorders {
                sectionScope.pageBorders { borderScope in
                    pageBorders.apply(to: borderScope)
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

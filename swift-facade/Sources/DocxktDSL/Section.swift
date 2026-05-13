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
///
/// Per-section headers and footers (one per page page type — default,
/// first, even — per section) land in dedicated `word/headerN.xml` /
/// `word/footerN.xml` parts and are referenced from this section's
/// inline `<w:sectPr>`:
///
/// ```swift
/// Section(
///     pageSize: .twips(width: 6000, height: 8000),
///     margins: .twips(top: 200, bottom: 200, left: 200, right: 200),
///     header: Header { Paragraph { Text("Page 1 of N") } },
///     footer: Footer { Paragraph { Text("draft").italic() } },
/// )
/// ```
public struct Section {
    /// Page orientation for the section. Sets A4 dimensions in the
    /// requested orientation. Mutually exclusive with `pageSize` —
    /// `pageSize`, when set, wins.
    public var orientation: PageOrientation?

    /// Explicit page size in twips (1 inch = 1440 twips). `nil` keeps
    /// whatever `orientation` resolves to (A4 portrait by default).
    /// Use this when the page must match a non-A4 aspect (e.g. one
    /// image per section with the page shrunk to the image's ratio).
    public var pageSize: PageSize?

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

    /// Per-section headers, keyed by page type. Each entry produces
    /// a dedicated `word/headerN.xml` part referenced from this
    /// section's inline `<w:sectPr>`. Empty = no header references.
    public var headers: [HeaderFooterType: Header]

    /// Per-section footers — same shape as `headers`.
    public var footers: [HeaderFooterType: Footer]

    public init(
        orientation: PageOrientation? = nil,
        pageSize: PageSize? = nil,
        columns: Int? = nil,
        type: SectionType? = nil,
        hasTitlePage: Bool = false,
        margins: PageMargins? = nil,
        pageBorders: PageBorders? = nil,
        header: Header? = nil,
        footer: Footer? = nil,
        headers: [HeaderFooterType: Header] = [:],
        footers: [HeaderFooterType: Footer] = [:],
    ) {
        self.orientation = orientation
        self.pageSize = pageSize
        self.columns = columns
        self.type = type
        self.hasTitlePage = hasTitlePage
        self.margins = margins
        self.pageBorders = pageBorders
        var hs = headers
        if let header { hs[.default] = header }
        self.headers = hs
        var fs = footers
        if let footer { fs[.default] = footer }
        self.footers = fs
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

    /// Which page type a header / footer applies to.
    public enum HeaderFooterType: Hashable {
        /// Every page that isn't covered by `.first` or `.even`.
        case `default`
        /// The first page of the section. Section-level title-page
        /// behavior (`<w:titlePg/>`) is OFF by default — set
        /// `hasTitlePage: true` on the `Section` to enable it.
        case first
        /// Even-numbered pages. Setting this auto-enables
        /// `<w:evenAndOddHeaders/>` in `word/settings.xml` (a global
        /// switch — applies document-wide once any section asks for it).
        case even
    }

    /// `<w:pgSz>` page size, in twips (1 inch = 1440 twips). The
    /// `orientation` field is informational on the wire (`w:orient`).
    public struct PageSize {
        public var widthTwips: Int
        public var heightTwips: Int
        public var orientation: PageOrientation

        public init(
            widthTwips: Int,
            heightTwips: Int,
            orientation: PageOrientation = .portrait,
        ) {
            self.widthTwips = widthTwips
            self.heightTwips = heightTwips
            self.orientation = orientation
        }

        /// Build from twips. Orientation defaults to portrait — flip to
        /// landscape only when the wire attribute matters to the
        /// consumer (Word infers visual orientation from width/height).
        public static func twips(
            width: Int,
            height: Int,
            orientation: PageOrientation = .portrait,
        ) -> PageSize {
            PageSize(widthTwips: width, heightTwips: height, orientation: orientation)
        }

        /// Build from inches (1 inch = 1440 twips).
        public static func inches(
            width: Double,
            height: Double,
            orientation: PageOrientation = .portrait,
        ) -> PageSize {
            PageSize(
                widthTwips: Int((width * 1440).rounded()),
                heightTwips: Int((height * 1440).rounded()),
                orientation: orientation,
            )
        }
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
            // Explicit pageSize wins over the orientation A4 preset.
            if let pageSize {
                sectionScope.pageSize(
                    widthTwips: Int32(pageSize.widthTwips),
                    heightTwips: Int32(pageSize.heightTwips),
                    orientation: pageSize.orientation.kotlin,
                )
            } else if let orientation {
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
            // Canonical order: DEFAULT → FIRST → EVEN. Matches the
            // emission order Kotlin's allocator uses, so byte-equal
            // parity stays trivial.
            for hfType in Section.HeaderFooterType.canonicalOrder {
                if let header = headers[hfType] {
                    sectionScope.header(type: hfType.kotlin) { headerScope in
                        header.applyToHeader(headerScope)
                    }
                }
            }
            for hfType in Section.HeaderFooterType.canonicalOrder {
                if let footer = footers[hfType] {
                    sectionScope.footer(type: hfType.kotlin) { footerScope in
                        footer.applyToFooter(footerScope)
                    }
                }
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

extension Section.PageOrientation {
    fileprivate var kotlin: Docxkt.PageOrientation {
        switch self {
        case .portrait: return .portrait
        case .landscape: return .landscape
        }
    }
}

extension Section.HeaderFooterType {
    fileprivate static let canonicalOrder: [Section.HeaderFooterType] = [.default, .first, .even]

    fileprivate var kotlin: HeaderFooterReferenceType {
        switch self {
        case .default: return HeaderFooterReferenceType.default_
        case .first: return HeaderFooterReferenceType.first
        case .even: return HeaderFooterReferenceType.even
        }
    }
}

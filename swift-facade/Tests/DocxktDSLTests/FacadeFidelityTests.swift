import XCTest
import Docxkt
@testable import DocxktDSL

/// Cross-translation fidelity battery. Each test builds the same
/// document twice — once through the SwiftUI-style `DocxktDSL` facade,
/// once through the raw bridged `import Docxkt` Kotlin API — and
/// asserts the resulting `.docx` byte streams are identical.
///
/// This complements the structural smoke tests in `DocxktDSLTests` by
/// proving the facade's translation to the underlying Kotlin DSL is
/// byte-faithful for every scope. Real OOXML correctness is already
/// covered by the 200+ fixture battery on the Kotlin side
/// (`:core:macosArm64Test` / `:core:iosSimulatorArm64Test`).
final class FacadeFidelityTests: XCTestCase {

    // MARK: - Per-scope cross-translation

    func testEmptyDocument() {
        assertSameBytes(
            facade: Document { Paragraph {} },
            raw: DocumentKt.document { scope in
                scope.paragraph { _ in }
            },
        )
    }

    func testPlainParagraph() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("Hello, world!") }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "Hello, world!")
                }
            },
        )
    }

    func testBoldRun() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("emphasized").bold() }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "emphasized") { runScope in
                        runScope.bold = KotlinBoolean(value: true)
                    }
                }
            },
        )
    }

    func testItalicRun() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("italic").italic() }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "italic") { runScope in
                        runScope.italics = KotlinBoolean(value: true)
                    }
                }
            },
        )
    }

    func testUnderlineRun() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("under").underline() }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "under") { runScope in
                        runScope.underline(type: UnderlineType.single, color: nil)
                    }
                }
            },
        )
    }

    func testCombinedModifiers() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("all three").bold().italic().underline() }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "all three") { runScope in
                        runScope.bold = KotlinBoolean(value: true)
                        runScope.italics = KotlinBoolean(value: true)
                        runScope.underline(type: UnderlineType.single, color: nil)
                    }
                }
            },
        )
    }

    func testHeaderAndFooter() {
        assertSameBytes(
            facade: Document {
                Header { Paragraph { Text("running head") } }
                Paragraph { Text("body") }
                Footer { Paragraph { Text("running foot") } }
            },
            raw: DocumentKt.document { scope in
                scope.header(type: HeaderFooterReferenceType.default_) { hs in
                    hs.paragraph { p in p.text(value: "running head") }
                }
                scope.paragraph { p in p.text(value: "body") }
                scope.footer(type: HeaderFooterReferenceType.default_) { fs in
                    fs.paragraph { p in p.text(value: "running foot") }
                }
            },
        )
    }

    func testTableRowCell() {
        assertSameBytes(
            facade: Document {
                Table {
                    Row {
                        Cell { Paragraph { Text("A1") } }
                        Cell { Paragraph { Text("B1") } }
                    }
                }
            },
            raw: DocumentKt.document { scope in
                scope.table { ts in
                    ts.row { rs in
                        rs.cell { cs in cs.paragraph { p in p.text(value: "A1") } }
                        rs.cell { cs in cs.paragraph { p in p.text(value: "B1") } }
                    }
                }
            },
        )
    }

    func testHyperlink() {
        assertSameBytes(
            facade: Document {
                Paragraph {
                    Text("Visit ")
                    Link("https://example.com") { Text("our site") }
                    Text(".")
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.text(value: "Visit ")
                    p.hyperlink(url: "https://example.com") { hs in
                        hs.text(value: "our site")
                    }
                    p.text(value: ".")
                }
            },
        )
    }

    func testProperties() {
        assertSameBytes(
            facade: Document {
                Properties(
                    title: "Q1 Report",
                    creator: "Jane Doe",
                    keywords: "finance",
                )
                Paragraph { Text("body") }
            },
            raw: DocumentKt.document { scope in
                scope.properties { ps in
                    ps.title = "Q1 Report"
                    ps.creator = "Jane Doe"
                    ps.keywords = "finance"
                }
                scope.paragraph { p in p.text(value: "body") }
            },
        )
    }

    func testParagraphStyleDeclarationAndUse() {
        assertSameBytes(
            facade: Document {
                ParagraphStyle(id: "Heading1", name: "Heading 1", basedOn: "Normal",
                               bold: true, size: 32, color: "1F3864")
                Paragraph(style: "Heading1") { Text("Q1 Report") }
            },
            raw: DocumentKt.document { scope in
                scope.paragraphStyle(id: "Heading1") { ss in
                    ss.name(value: "Heading 1")
                    ss.basedOn(value: "Normal")
                    ss.run { rs in
                        rs.bold = KotlinBoolean(value: true)
                        rs.size = KotlinInt(int: 32)
                        rs.color = "1F3864"
                    }
                }
                scope.paragraph { p in
                    p.styleReference = "Heading1"
                    p.text(value: "Q1 Report")
                }
            },
        )
    }

    func testCharacterStyleDeclarationAndUse() {
        assertSameBytes(
            facade: Document {
                CharacterStyle(id: "Emphasis", italic: true, color: "C00000")
                Paragraph {
                    Text("plain ")
                    Text("emphasized").styled("Emphasis")
                }
            },
            raw: DocumentKt.document { scope in
                scope.characterStyle(id: "Emphasis") { ss in
                    ss.run { rs in
                        rs.italics = KotlinBoolean(value: true)
                        rs.color = "C00000"
                    }
                }
                scope.paragraph { p in
                    p.text(value: "plain ")
                    p.text(value: "emphasized") { runScope in
                        runScope.styleReference = "Emphasis"
                    }
                }
            },
        )
    }

    func testBulletList() {
        assertSameBytes(
            facade: Document {
                ListTemplate(id: "Bullets",
                    ListTemplate.Level(0, format: .bullet, text: "•",
                                       indent: 720, hanging: 360),
                )
                Paragraph(numbering: NumberingReference("Bullets", level: 0)) {
                    Text("First")
                }
                Paragraph(numbering: NumberingReference("Bullets", level: 0)) {
                    Text("Second")
                }
            },
            raw: DocumentKt.document { scope in
                scope.listTemplate(reference: "Bullets") { ts in
                    ts.level(
                        level: 0,
                        format: LevelFormat.bullet,
                        text: "•",
                        start: 1,
                        justification: AlignmentType.left,
                        indentLeft: KotlinInt(int: 720),
                        indentHanging: KotlinInt(int: 360),
                        configure: { _ in },
                    )
                }
                scope.paragraph { p in
                    p.numbering(reference: "Bullets", level: 0, instance: 0)
                    p.text(value: "First")
                }
                scope.paragraph { p in
                    p.numbering(reference: "Bullets", level: 0, instance: 0)
                    p.text(value: "Second")
                }
            },
        )
    }

    func testInlineImage() {
        let png = Self.tinyPng
        assertSameBytes(
            facade: Document {
                Paragraph {
                    Image(data: png, widthEmus: 9525, heightEmus: 9525, format: .png,
                          description: "stub")
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.image(
                        bytes: png.toKotlinByteArrayForTest(),
                        widthEmus: 9525,
                        heightEmus: 9525,
                        format: ImageFormat.png,
                        description: "stub",
                    )
                }
            },
        )
    }

    func testAnchorImage() {
        let png = Self.tinyPng
        assertSameBytes(
            facade: Document {
                Paragraph {
                    AnchorImage(
                        data: png,
                        widthEmus: 1_905_000, heightEmus: 952_500,
                        format: .png,
                    ) { anchor in
                        anchor.positionH(relativeFrom: .column, offsetEmus: 0)
                        anchor.positionV(relativeFrom: .paragraph, offsetEmus: 0)
                        anchor.wrapSquare(side: .bothSides)
                        anchor.behindDoc(false)
                    }
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.imageAnchor(
                        bytes: png.toKotlinByteArrayForTest(),
                        widthEmus: 1_905_000,
                        heightEmus: 952_500,
                        format: ImageFormat.png,
                        description: nil,
                    ) { anchorScope in
                        anchorScope.positionH(relativeFrom: HorizontalRelativeFrom.column, offsetEmus: 0)
                        anchorScope.positionV(relativeFrom: VerticalRelativeFrom.paragraph, offsetEmus: 0)
                        anchorScope.wrapSquare(
                            side: WrapSide.bothSides,
                            marginTopEmus: 0,
                            marginBottomEmus: 0,
                            marginLeftEmus: 0,
                            marginRightEmus: 0,
                        )
                        anchorScope.behindDoc = false
                    }
                }
            },
        )
    }

    func testTextbox() {
        assertSameBytes(
            facade: Document {
                Paragraph {
                    Textbox(widthEmus: 1_905_000, heightEmus: 952_500) {
                        Paragraph { Text("inside box").bold() }
                    }
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { outer in
                    outer.textbox(
                        widthEmus: 1_905_000,
                        heightEmus: 952_500,
                        description: nil,
                    ) { ts in
                        ts.paragraph { p in
                            p.text(value: "inside box") { runScope in
                                runScope.bold = KotlinBoolean(value: true)
                            }
                        }
                    }
                }
            },
        )
    }

    func testSectionBreakLandscape() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("portrait") }
                Section(orientation: .landscape, columns: 2)
                Paragraph { Text("landscape, two-column") }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in p.text(value: "portrait") }
                scope.sectionBreak { ss in
                    ss.a4Landscape()
                    ss.columns(
                        count: 2,
                        equalWidth: KotlinBoolean(value: true),
                        spaceTwips: nil,
                        separator: nil,
                        individual: [],
                    )
                }
                scope.paragraph { p in p.text(value: "landscape, two-column") }
            },
        )
    }

    func testSectionBreakWithMarginsTwips() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("before") }
                Section(margins: Section.PageMargins(
                    top: 720, right: 1080, bottom: 720, left: 1080,
                    header: 360, footer: 360, gutter: 0,
                ))
                Paragraph { Text("after") }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in p.text(value: "before") }
                scope.sectionBreak { ss in
                    ss.margins(
                        top: KotlinInt(int: 720),
                        right: KotlinInt(int: 1080),
                        bottom: KotlinInt(int: 720),
                        left: KotlinInt(int: 1080),
                        header: KotlinInt(int: 360),
                        footer: KotlinInt(int: 360),
                        gutter: KotlinInt(int: 0),
                    )
                }
                scope.paragraph { p in p.text(value: "after") }
            },
        )
    }

    func testSectionBreakWithMarginsInches() {
        assertSameBytes(
            facade: Document {
                Paragraph { Text("before") }
                Section(
                    orientation: .landscape,
                    margins: .inches(top: 0.5, right: 0.75, bottom: 0.5, left: 0.75),
                )
                Paragraph { Text("after") }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in p.text(value: "before") }
                scope.sectionBreak { ss in
                    ss.a4Landscape()
                    ss.margins(
                        top: KotlinInt(int: 720),
                        right: KotlinInt(int: 1080),
                        bottom: KotlinInt(int: 720),
                        left: KotlinInt(int: 1080),
                        header: KotlinInt(int: 706),
                        footer: KotlinInt(int: 706),
                        gutter: KotlinInt(int: 0),
                    )
                }
                scope.paragraph { p in p.text(value: "after") }
            },
        )
    }

    func testFootnoteAndReference() {
        assertSameBytes(
            facade: Document {
                Footnote(id: 1) { Paragraph { Text("Source: ...") } }
                Paragraph {
                    Text("See ")
                    FootnoteReference(id: 1)
                    Text(" for details.")
                }
            },
            raw: DocumentKt.document { scope in
                scope.footnote(id: 1) { fs in
                    fs.paragraph { p in p.text(value: "Source: ...") }
                }
                scope.paragraph { p in
                    p.text(value: "See ")
                    p.footnoteReference(id: 1)
                    p.text(value: " for details.")
                }
            },
        )
    }

    func testEndnoteAndReference() {
        assertSameBytes(
            facade: Document {
                Endnote(id: 1) { Paragraph { Text("End note text.") } }
                Paragraph {
                    Text("Concluding ")
                    EndnoteReference(id: 1)
                    Text(".")
                }
            },
            raw: DocumentKt.document { scope in
                scope.endnote(id: 1) { fs in
                    fs.paragraph { p in p.text(value: "End note text.") }
                }
                scope.paragraph { p in
                    p.text(value: "Concluding ")
                    p.endnoteReference(id: 1)
                    p.text(value: ".")
                }
            },
        )
    }

    func testCommentRangeAndReference() {
        assertSameBytes(
            facade: Document {
                Comment(id: 1, author: "R", initials: "R",
                        date: "2026-04-30T10:00:00Z") {
                    Paragraph { Text("Please rephrase.") }
                }
                Paragraph {
                    Text("Lead-in. ")
                    CommentRangeStart(id: 1)
                    Text("This phrase needs review")
                    CommentRangeEnd(id: 1)
                    CommentReference(id: 1)
                    Text(".")
                }
            },
            raw: DocumentKt.document { scope in
                scope.comment(
                    id: 1,
                    author: "R",
                    initials: "R",
                    date: "2026-04-30T10:00:00Z",
                ) { cs in
                    cs.paragraph { p in p.text(value: "Please rephrase.") }
                }
                scope.paragraph { p in
                    p.text(value: "Lead-in. ")
                    p.commentRangeStart(id: 1)
                    p.text(value: "This phrase needs review")
                    p.commentRangeEnd(id: 1)
                    p.commentReference(id: 1)
                    p.text(value: ".")
                }
            },
        )
    }

    // MARK: - Combo / cross-scope coverage

    func testParagraphStyleAndNumberingOnSameDoc() {
        assertSameBytes(
            facade: Document {
                ParagraphStyle(id: "H1", basedOn: "Normal", bold: true, size: 28)
                ListTemplate(id: "Bullets",
                    ListTemplate.Level(0, format: .bullet, text: "•",
                                       indent: 720, hanging: 360),
                )
                Paragraph(style: "H1") { Text("Heading") }
                Paragraph(numbering: NumberingReference("Bullets", level: 0)) {
                    Text("Item one")
                }
                Paragraph(numbering: NumberingReference("Bullets", level: 0)) {
                    Text("Item two")
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraphStyle(id: "H1") { ss in
                    ss.basedOn(value: "Normal")
                    ss.run { rs in
                        rs.bold = KotlinBoolean(value: true)
                        rs.size = KotlinInt(int: 28)
                    }
                }
                scope.listTemplate(reference: "Bullets") { ts in
                    ts.level(
                        level: 0, format: LevelFormat.bullet, text: "•", start: 1,
                        justification: AlignmentType.left,
                        indentLeft: KotlinInt(int: 720),
                        indentHanging: KotlinInt(int: 360),
                        configure: { _ in },
                    )
                }
                scope.paragraph { p in
                    p.styleReference = "H1"
                    p.text(value: "Heading")
                }
                scope.paragraph { p in
                    p.numbering(reference: "Bullets", level: 0, instance: 0)
                    p.text(value: "Item one")
                }
                scope.paragraph { p in
                    p.numbering(reference: "Bullets", level: 0, instance: 0)
                    p.text(value: "Item two")
                }
            },
        )
    }

    func testTableCellsWithStyledRuns() {
        assertSameBytes(
            facade: Document {
                Table {
                    Row {
                        Cell { Paragraph { Text("Header A").bold() } }
                        Cell { Paragraph { Text("Header B").bold() } }
                    }
                    Row {
                        Cell { Paragraph { Text("plain a") } }
                        Cell { Paragraph { Text("italic b").italic() } }
                    }
                }
            },
            raw: DocumentKt.document { scope in
                scope.table { ts in
                    ts.row { rs in
                        rs.cell { cs in
                            cs.paragraph { p in
                                p.text(value: "Header A") { rsx in
                                    rsx.bold = KotlinBoolean(value: true)
                                }
                            }
                        }
                        rs.cell { cs in
                            cs.paragraph { p in
                                p.text(value: "Header B") { rsx in
                                    rsx.bold = KotlinBoolean(value: true)
                                }
                            }
                        }
                    }
                    ts.row { rs in
                        rs.cell { cs in
                            cs.paragraph { p in p.text(value: "plain a") }
                        }
                        rs.cell { cs in
                            cs.paragraph { p in
                                p.text(value: "italic b") { rsx in
                                    rsx.italics = KotlinBoolean(value: true)
                                }
                            }
                        }
                    }
                }
            },
        )
    }

    func testHeaderContainingTable() {
        assertSameBytes(
            facade: Document {
                Header {
                    Table {
                        Row {
                            Cell { Paragraph { Text("logo") } }
                            Cell { Paragraph { Text("running title") } }
                        }
                    }
                }
                Paragraph { Text("body") }
            },
            raw: DocumentKt.document { scope in
                scope.header(type: HeaderFooterReferenceType.default_) { hs in
                    hs.table { ts in
                        ts.row { rs in
                            rs.cell { cs in
                                cs.paragraph { p in p.text(value: "logo") }
                            }
                            rs.cell { cs in
                                cs.paragraph { p in p.text(value: "running title") }
                            }
                        }
                    }
                }
                scope.paragraph { p in p.text(value: "body") }
            },
        )
    }

    func testLinkInsideStyledParagraph() {
        assertSameBytes(
            facade: Document {
                ParagraphStyle(id: "Body", bold: false, size: 22)
                Paragraph(style: "Body") {
                    Text("See ")
                    Link("https://example.com") { Text("docs").bold() }
                    Text(" for details.")
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraphStyle(id: "Body") { ss in
                    ss.run { rs in
                        rs.bold = KotlinBoolean(value: false)
                        rs.size = KotlinInt(int: 22)
                    }
                }
                scope.paragraph { p in
                    p.styleReference = "Body"
                    p.text(value: "See ")
                    p.hyperlink(url: "https://example.com") { hs in
                        hs.text(value: "docs") { runScope in
                            runScope.bold = KotlinBoolean(value: true)
                        }
                    }
                    p.text(value: " for details.")
                }
            },
        )
    }

    func testCommentSpansMultipleRuns() {
        assertSameBytes(
            facade: Document {
                Comment(id: 1, author: "R", date: "2026-04-30T10:00:00Z") {
                    Paragraph { Text("Multi-run review.") }
                }
                Paragraph {
                    Text("Pre. ")
                    CommentRangeStart(id: 1)
                    Text("first run").bold()
                    Text(" middle ")
                    Text("third run").italic()
                    CommentRangeEnd(id: 1)
                    CommentReference(id: 1)
                    Text(" post.")
                }
            },
            raw: DocumentKt.document { scope in
                scope.comment(
                    id: 1, author: "R", initials: nil, date: "2026-04-30T10:00:00Z",
                ) { cs in
                    cs.paragraph { p in p.text(value: "Multi-run review.") }
                }
                scope.paragraph { p in
                    p.text(value: "Pre. ")
                    p.commentRangeStart(id: 1)
                    p.text(value: "first run") { runScope in
                        runScope.bold = KotlinBoolean(value: true)
                    }
                    p.text(value: " middle ")
                    p.text(value: "third run") { runScope in
                        runScope.italics = KotlinBoolean(value: true)
                    }
                    p.commentRangeEnd(id: 1)
                    p.commentReference(id: 1)
                    p.text(value: " post.")
                }
            },
        )
    }

    func testAnchorImageWithAlignAndOffset() {
        let png = Self.tinyPng
        assertSameBytes(
            facade: Document {
                Paragraph {
                    AnchorImage(
                        data: png,
                        widthEmus: 1_905_000, heightEmus: 952_500,
                        format: .png,
                    ) { anchor in
                        anchor.positionH(relativeFrom: .page, align: .center)
                        anchor.positionV(relativeFrom: .page, offsetEmus: 914_400)
                        anchor.wrapTopAndBottom(marginTopEmus: 0, marginBottomEmus: 0)
                    }
                }
            },
            raw: DocumentKt.document { scope in
                scope.paragraph { p in
                    p.imageAnchor(
                        bytes: png.toKotlinByteArrayForTest(),
                        widthEmus: 1_905_000, heightEmus: 952_500,
                        format: ImageFormat.png, description: nil,
                    ) { anchorScope in
                        anchorScope.positionH(
                            relativeFrom: HorizontalRelativeFrom.page,
                            align: HorizontalAlign.center,
                        )
                        anchorScope.positionV(
                            relativeFrom: VerticalRelativeFrom.page,
                            offsetEmus: 914_400,
                        )
                        anchorScope.wrapTopAndBottom(
                            marginTopEmus: 0, marginBottomEmus: 0,
                        )
                    }
                }
            },
        )
    }

    func testTextboxInsideHeader() {
        assertSameBytes(
            facade: Document {
                Header {
                    Paragraph {
                        Textbox(widthEmus: 1_905_000, heightEmus: 600_000) {
                            Paragraph { Text("Quarterly").bold() }
                        }
                    }
                }
                Paragraph { Text("body") }
            },
            raw: DocumentKt.document { scope in
                scope.header(type: HeaderFooterReferenceType.default_) { hs in
                    hs.paragraph { outer in
                        outer.textbox(
                            widthEmus: 1_905_000, heightEmus: 600_000, description: nil,
                        ) { ts in
                            ts.paragraph { p in
                                p.text(value: "Quarterly") { runScope in
                                    runScope.bold = KotlinBoolean(value: true)
                                }
                            }
                        }
                    }
                }
                scope.paragraph { p in p.text(value: "body") }
            },
        )
    }

    func testListTemplateWithDecimalFormat() {
        assertSameBytes(
            facade: Document {
                ListTemplate(id: "Numbered",
                    ListTemplate.Level(0, format: .decimal, text: "%1.",
                                       indent: 720, hanging: 360),
                    ListTemplate.Level(1, format: .lowerLetter, text: "%2)",
                                       indent: 1440, hanging: 360),
                )
                Paragraph(numbering: NumberingReference("Numbered", level: 0)) {
                    Text("Top item")
                }
                Paragraph(numbering: NumberingReference("Numbered", level: 1)) {
                    Text("Nested")
                }
            },
            raw: DocumentKt.document { scope in
                scope.listTemplate(reference: "Numbered") { ts in
                    ts.level(
                        level: 0, format: LevelFormat.decimal, text: "%1.", start: 1,
                        justification: AlignmentType.left,
                        indentLeft: KotlinInt(int: 720),
                        indentHanging: KotlinInt(int: 360),
                        configure: { _ in },
                    )
                    ts.level(
                        level: 1, format: LevelFormat.lowerLetter, text: "%2)", start: 1,
                        justification: AlignmentType.left,
                        indentLeft: KotlinInt(int: 1440),
                        indentHanging: KotlinInt(int: 360),
                        configure: { _ in },
                    )
                }
                scope.paragraph { p in
                    p.numbering(reference: "Numbered", level: 0, instance: 0)
                    p.text(value: "Top item")
                }
                scope.paragraph { p in
                    p.numbering(reference: "Numbered", level: 1, instance: 0)
                    p.text(value: "Nested")
                }
            },
        )
    }

    func testSectionBetweenContentGroups() {
        assertSameBytes(
            facade: Document {
                Properties(title: "Multi-section")
                Paragraph { Text("First section").bold() }
                Section(orientation: .landscape)
                Paragraph { Text("Second section in landscape") }
                Section(orientation: .portrait, hasTitlePage: true)
                Paragraph { Text("Third section, title page") }
            },
            raw: DocumentKt.document { scope in
                scope.properties { ps in ps.title = "Multi-section" }
                scope.paragraph { p in
                    p.text(value: "First section") { runScope in
                        runScope.bold = KotlinBoolean(value: true)
                    }
                }
                scope.sectionBreak { ss in ss.a4Landscape() }
                scope.paragraph { p in p.text(value: "Second section in landscape") }
                scope.sectionBreak { ss in
                    ss.a4Portrait()
                    ss.titlePage()
                }
                scope.paragraph { p in p.text(value: "Third section, title page") }
            },
        )
    }

    // MARK: - Helpers

    private static let tinyPng: Data = Data(base64Encoded:
        "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkAAIAAAoAAv/lxKUAAAAASUVORK5CYII="
    )!

    private func assertSameBytes(
        facade: DocxktDSL.Document,
        raw: Docxkt.Document,
        file: StaticString = #file,
        line: UInt = #line,
    ) {
        let facadeBytes = facade.toData()
        let rawBytes = raw.toByteArray().toDataForTest()
        let facadeEntries = unzipForCompare(facadeBytes)
        let rawEntries = unzipForCompare(rawBytes)

        XCTAssertEqual(
            facadeEntries.keys.sorted(),
            rawEntries.keys.sorted(),
            "ZIP entry path set mismatch",
            file: file, line: line,
        )

        for (path, facadeContent) in facadeEntries {
            guard let rawContent = rawEntries[path] else { continue }
            XCTAssertEqual(
                facadeContent, rawContent,
                "\(path): bytes differ (\(facadeContent.count) vs \(rawContent.count))",
                file: file, line: line,
            )
        }
    }

    /// Crack a `.docx` byte stream into `path -> bytes`, dropping
    /// time-dependent entries (`docProps/core.xml` carries
    /// `dcterms:created` / `dcterms:modified` from the Kotlin
    /// `nowIso8601()` default — between two `Document { … }`
    /// constructions the timestamps drift across millisecond
    /// boundaries, perturbing the entire ZIP byte stream).
    private func unzipForCompare(_ bytes: Data) -> [String: Data] {
        let kotlinBytes = bytes.toKotlinByteArrayForTest()
        let entries = ZipReader.shared.read(bytes: kotlinBytes)
        var result: [String: Data] = [:]
        for (path, kotlinBytes) in entries {
            if path == "docProps/core.xml" { continue }
            result[path] = kotlinBytes.toDataForTest()
        }
        return result
    }
}

extension KotlinByteArray {
    fileprivate func toDataForTest() -> Data {
        let count = Int(size)
        var data = Data(count: count)
        data.withUnsafeMutableBytes { dst in
            guard let dstBase = dst.bindMemory(to: Int8.self).baseAddress else { return }
            for i in 0..<count {
                dstBase[i] = self.get(index: Int32(i))
            }
        }
        return data
    }
}

extension Data {
    fileprivate func toKotlinByteArrayForTest() -> KotlinByteArray {
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

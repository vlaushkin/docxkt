import XCTest
@testable import DocxktDSL

final class DocxktDSLTests: XCTestCase {

    func testEmptyDocumentSerializesToValidZIP() throws {
        let doc = Document {
            Paragraph { Text("") }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200, "expected > 200 bytes, got \(data.count)")
        XCTAssertEqual(data[0], 0x50)  // 'P'
        XCTAssertEqual(data[1], 0x4B)  // 'K'
        XCTAssertEqual(data[2], 0x03)
        XCTAssertEqual(data[3], 0x04)
    }

    func testHelloWorldParagraph() throws {
        let doc = Document {
            Paragraph { Text("Hello, world!") }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testInlineFormattingComposes() throws {
        let doc = Document {
            Paragraph {
                Text("plain")
                Text("bold").bold()
                Text("italic").italic()
                Text("under").underline()
                Text("all three").bold().italic().underline()
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testTableWithTwoCells() throws {
        let doc = Document {
            Table {
                Row {
                    Cell { Paragraph { Text("A1") } }
                    Cell { Paragraph { Text("B1") } }
                }
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 300)
    }

    func testHeaderAndFooter() throws {
        let doc = Document {
            Header { Paragraph { Text("running head") } }
            Paragraph { Text("body") }
            Footer { Paragraph { Text("page footer") } }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 400)
    }

    func testHyperlinkInline() throws {
        let doc = Document {
            Paragraph {
                Text("Visit ")
                Link("https://example.com") { Text("our site").bold() }
                Text(" for details.")
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testProperties() throws {
        let doc = Document {
            Properties(
                title: "Q1 Report",
                creator: "Jane Doe",
                keywords: "finance,quarterly",
                custom: ["department": "Sales"],
            )
            Paragraph { Text("Body") }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testParagraphAndCharacterStyles() throws {
        let doc = Document {
            ParagraphStyle(id: "Heading1", name: "Heading 1", basedOn: "Normal",
                           bold: true, size: 32, color: "1F3864")
            CharacterStyle(id: "Emphasis", italic: true, color: "C00000")
            Paragraph(style: "Heading1") { Text("Q1 Report") }
            Paragraph {
                Text("Revenue grew ")
                Text("12%").styled("Emphasis")
                Text(" QoQ.")
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testInlineImage() throws {
        // 1x1 transparent PNG (smallest valid PNG; 67 bytes)
        let png = Data(base64Encoded: "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkAAIAAAoAAv/lxKUAAAAASUVORK5CYII=")!
        let doc = Document {
            Paragraph {
                Image(data: png, widthEmus: 9525, heightEmus: 9525, format: .png,
                      description: "1×1 stub")
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 300)
    }

    func testBulletList() throws {
        let doc = Document {
            ListTemplate(id: "MyBullets",
                ListTemplate.Level(0, format: .bullet, text: "•",
                                   indent: 720, hanging: 360),
                ListTemplate.Level(1, format: .bullet, text: "○",
                                   indent: 1440, hanging: 360),
            )
            Paragraph(numbering: NumberingReference("MyBullets", level: 0)) {
                Text("First item")
            }
            Paragraph(numbering: NumberingReference("MyBullets", level: 1)) {
                Text("Nested item")
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 300)
    }

    func testFootnoteAndEndnote() throws {
        let doc = Document {
            Footnote(id: 1) { Paragraph { Text("First footnote.") } }
            Endnote(id: 1) { Paragraph { Text("First endnote.") } }
            Paragraph {
                Text("See note")
                FootnoteReference(id: 1)
                Text(" and endnote")
                EndnoteReference(id: 1)
                Text(".")
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testCommentRange() throws {
        let doc = Document {
            Comment(
                id: 1,
                author: "Reviewer",
                initials: "R",
                date: "2026-04-30T10:00:00Z",
            ) {
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
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testAnchorImage() throws {
        let png = Data(base64Encoded: "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNkAAIAAAoAAv/lxKUAAAAASUVORK5CYII=")!
        let doc = Document {
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
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 300)
    }

    func testTextbox() throws {
        let doc = Document {
            Paragraph {
                Textbox(widthEmus: 1_905_000, heightEmus: 952_500) {
                    Paragraph { Text("Inside the box").bold() }
                }
            }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testSectionBreak() throws {
        let doc = Document {
            Paragraph { Text("portrait body") }
            Section(orientation: .landscape, columns: 2, hasTitlePage: true)
            Paragraph { Text("landscape, two-column body") }
        }
        let data = doc.toData()
        XCTAssertGreaterThan(data.count, 200)
    }

    func testWriteToURL() throws {
        let doc = Document {
            Paragraph { Text("file write") }
        }
        let tempURL = FileManager.default.temporaryDirectory
            .appendingPathComponent("docxkt-dsl-test-\(UUID()).docx")
        try doc.write(to: tempURL)
        defer { try? FileManager.default.removeItem(at: tempURL) }

        let onDisk = try Data(contentsOf: tempURL)
        XCTAssertGreaterThan(onDisk.count, 100)
        XCTAssertEqual(onDisk[0], 0x50)
        XCTAssertEqual(onDisk[1], 0x4B)
    }
}

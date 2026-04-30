# DocxktDSL — Swift facade for docxkt

A SwiftUI-style declarative DSL for building `.docx` documents on iOS and
macOS. Wraps the [docxkt](../README.md) Kotlin Multiplatform library
through a binary `XCFramework` and exposes a thin `@resultBuilder` API.

## Install (local development)

Build the underlying `Docxkt.xcframework` from the repo root, then point
your app's `Package.swift` at this folder:

```bash
./gradlew :core:assembleDocxktReleaseXCFramework
```

```swift
// Your app's Package.swift
.package(path: "../docxkt/swift-facade"),
// then in target dependencies:
.product(name: "DocxktDSL", package: "swift-facade"),
```

## Usage

```swift
import DocxktDSL

let doc = Document {
    Header {
        Paragraph { Text("Q1 Report").bold() }
    }
    Paragraph {
        Text("Revenue grew ").italic()
        Text("12%").bold()
        Text(" quarter over quarter.")
    }
    Table {
        Row {
            Cell { Paragraph { Text("Product").bold() } }
            Cell { Paragraph { Text("Units").bold() } }
        }
        Row {
            Cell { Paragraph { Text("Widgets") } }
            Cell { Paragraph { Text("1,420") } }
        }
    }
    Footer {
        Paragraph { Text("Confidential — internal only").italic() }
    }
}

let url = URL.documentsDirectory.appending(path: "report.docx")
try doc.write(to: url)
```

## Supported scopes

Top-level (children of `Document { … }`):

- `Paragraph { … }` — paragraph holding inline runs
- `Paragraph(style: "id") { … }` — references a `ParagraphStyle`
- `Paragraph(numbering: NumberingReference("id", level: N)) { … }` — list item
- `Table { Row { Cell { … } } }` — basic tables
- `Header { … }` / `Footer { … }` — default page header/footer
- `Section(orientation:, columns:, type:, hasTitlePage:)` — section break
- `Properties(title:, creator:, keywords:, custom:, …)` — document metadata
- `ParagraphStyle(id:, name:, basedOn:, bold:, italic:, size:, color:, fontFamily:, …)` — style definition
- `CharacterStyle(id:, …)` — run-style definition
- `ListTemplate(id: "id", Level(0, format: .bullet, text: "•", indent:, hanging:))` — list template

Inline (children of `Paragraph { … }`):

- `Text("…")` with `.bold()` / `.italic()` / `.underline()` / `.styled("id")` modifiers
- `Link("https://…") { Text("…") }` — external hyperlink
- `Image(data:, widthEmus:, heightEmus:, format: .png/.jpeg/.gif/.bmp, description:)` — inline image
- `AnchorImage(data:, widthEmus:, heightEmus:, format:) { anchor in … }` — floating image with positioning + wrap
- `Textbox(widthEmus:, heightEmus:) { Paragraph { … } }` — inline textbox

Annotations (footnotes, endnotes, comments):

- `Footnote(id:) { Paragraph { … } }` — footnote definition (top-level)
- `Endnote(id:) { Paragraph { … } }` — endnote definition (top-level)
- `Comment(id:, author:, initials:, date:) { Paragraph { … } }` — comment definition (top-level)
- `FootnoteReference(id:)` / `EndnoteReference(id:)` — inline reference markers
- `CommentRangeStart(id:)` / `CommentRangeEnd(id:)` / `CommentReference(id:)` — inline comment-range markers

## Reaching the raw Kotlin API

The facade covers every scope upstream's TypeScript DSL exposes. If
you need a feature not yet in the Swift surface (e.g. line numbering,
tabs, math, frame positioning), drop into the bridged Kotlin types
directly:

```swift
import Docxkt

let doc = DocumentKt.document { scope in
    scope.paragraph { p in
        p.text(value: "raw bridging gives you everything")
    }
    // call any DocumentScope method directly
}
```

The `import Docxkt` module exposes the full bridged Kotlin API
(~100 types). Mix-and-match: build common shapes via the facade, then
post-process via raw bridging if a niche tweak is needed.

## Platform support

- iOS 13+
- macOS 11+ (Apple Silicon)

iOS Simulator on Intel Macs is supported by the bundled
`ios-arm64_x86_64-simulator` slice in the XCFramework.

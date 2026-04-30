# docxkt

Kotlin Multiplatform library for generating Microsoft Word `.docx`
files. Targets JVM, Android, iOS, and macOS. Port of
[dolanmiu/docx](https://github.com/dolanmiu/docx) (TypeScript) to
idiomatic Kotlin, with a DSL-first public API and an XMLUnit
golden-fixture test harness pinning the wire to upstream.

## Status

**v1.1.1** — Kotlin Multiplatform release. JVM and Android consumers
keep the v1 API; iOS / macOS land via an Apple `XCFramework` and an
optional SwiftUI-style facade Swift Package.

- ~95% of the OOXML feature surface byte-equal vs upstream
  dolanmiu/docx via golden fixtures.
- 200+ fixture-driven tests run on `:core:jvmTest`, `:core:macosArm64Test`,
  and `:core:iosSimulatorArm64Test` — Apple-Native bytes are byte-
  identical to JVM bytes which are byte-identical to upstream.
- Patcher (read-modify-write existing `.docx` templates) ships with
  the same upstream-parity coverage.
- Swift facade covers every public Kotlin DSL scope; iOS and macOS
  consumers can choose the SwiftUI-style API or drop into raw KMP→
  Swift bridging.

## Install

### JVM / Android (Gradle)

```kotlin
dependencies {
    implementation("io.github.vlaushkin:docxkt-core:1.1.0")     // KMP, picks JVM/Android variant
    implementation("io.github.vlaushkin:docxkt-patcher:1.1.0")  // template patching (optional)
}
```

The Android conveniences (FileProvider helpers, MediaStore
saveToDownloads) live inside `docxkt-core`'s Android variant —
Gradle's variant-aware resolution picks the right artifact for your
consumer target. (The standalone `docxkt-android` artifact from v1.x
no longer exists.)

### iOS / macOS (Swift Package Manager)

In your app's `Package.swift`:

```swift
.package(url: "https://github.com/vlaushkin/docxkt", from: "1.1.0"),
// in target dependencies:
.product(name: "DocxktDSL", package: "docxkt"),
```

Or via Xcode UI: **File → Add Package Dependencies →** paste
`https://github.com/vlaushkin/docxkt`.

The `DocxktDSL` library wraps the bundled `Docxkt.xcframework` (iOS
device / iOS simulator / macOS arm64 slices) with a SwiftUI-style
`@resultBuilder` API. Consumers preferring the raw bridged Kotlin
types can `import Docxkt` directly.

**macOS arm64-only:** the macOS slice ships only `arm64` (Kotlin/Native
deprecated `macosX64` in 2.3.20). Intel Mac consumers must set
`EXCLUDED_ARCHS[sdk=macosx*] = x86_64` on their target.

**Local development** (consuming a working tree, not a published
release) — point the SPM dependency at the repo root and rebuild the
XCFramework first:

```bash
./gradlew :core:assembleDocxktReleaseXCFramework
```

```swift
.package(path: "<path-to-docxkt>"),
```

## Quick start

### Kotlin (JVM / Android)

```kotlin
import io.docxkt.api.document
import io.docxkt.api.writeTo
import io.docxkt.model.drawing.ImageFormat
import java.io.File

val logo = File("logo.png").readBytes()

val doc = document {
    paragraph {
        text("Report: ") { bold = true }
        text("Q4 2026 summary")
    }
    table {
        width(io.docxkt.model.table.TableWidth.pct(5000))
        row {
            cell { paragraph { text("Metric") } }
            cell { paragraph { text("Value") } }
        }
        row {
            cell { paragraph { text("Users") } }
            cell { paragraph { text("1,240") } }
        }
    }
    paragraph {
        image(logo, widthEmus = 2_000_000, heightEmus = 1_000_000,
              format = ImageFormat.PNG)
    }
}

File("report.docx").outputStream().use { doc.writeTo(it) }
```

### Android

The Android variant of `docxkt-core` ships extension helpers for
MediaStore / FileProvider integration:

```kotlin
import io.docxkt.android.saveToDownloads
import io.docxkt.android.buildShareIntent

val uri: Uri = doc.saveToDownloads(this, displayName = "report.docx")
startActivity(doc.buildShareIntent(this, displayName = "report.docx"))
```

On API 29+ the file lands in the system Downloads folder via
MediaStore (no `WRITE_EXTERNAL_STORAGE` required). On older APIs the
helper falls through to a legacy path.

### Swift (iOS / macOS)

```swift
import DocxktDSL

let doc = Document {
    Header { Paragraph { Text("Q1 Report").bold() } }
    Paragraph {
        Text("Revenue grew ").italic()
        Text("12%").bold()
        Text(" quarter over quarter.")
    }
    Table(width: .pct(5000), columnWidths: [3000, 3000, 3000]) {
        Row {
            Cell { Paragraph { Text("Product").bold() } }
            Cell { Paragraph { Text("Units").bold() } }
        }
        Row {
            Cell { Paragraph { Text("Widgets") } }
            Cell { Paragraph { Text("1,420") } }
        }
    }
    Footer { Paragraph { Text("Confidential").italic() } }
}

try doc.write(to: outputURL)
```

`Section` accepts `orientation`, `columns`, `type`, `hasTitlePage`, and
`margins` (twips, or `.inches(...)` / `.cm(...)` factories) — full parity
with the Kotlin `sectionBreak { … }` scope's most-used surface.

A complete iOS + macOS sample app lives at `sample-apple/`. Open
`sample-apple.xcodeproj` and run the `SampleApple` scheme on either
destination.

### Patching templates (`:patcher`)

```kotlin
import io.docxkt.patcher.PatchDocument
import io.docxkt.patcher.Patch
import io.docxkt.api.runs

val template = File("template.docx").readBytes()
val patched = PatchDocument.patch(
    template,
    mapOf(
        "name"  to Patch.Text("Alice"),
        "title" to Patch.ParagraphInline(runs { run("CEO") { bold = true } }),
    ),
)
File("filled.docx").writeBytes(patched)
```

Patcher supports `Text`, `ParagraphInline`, `Paragraphs`, `Rows`,
`Image`, and `DOCUMENT` patch types, including hyperlinks and images
inside inline patches and cross-part patching for headers/footers.

## Architecture

Two production modules:

- **`:core`** — Kotlin Multiplatform. `commonMain` holds the DSL and
  XML emission; `jvmAndAndroidMain` carries the `java.util.zip` /
  `java.time.Instant` paths; `appleMain` binds libz via Kotlin/Native's
  bundled `platform.zlib` interop and uses `NSISO8601DateFormatter`
  for timestamps. `commonMain` follows a stdlib-first preference;
  the only Kotlin runtime dep is `kotlinx-io` (used for the public
  `Sink` type on `Document.writeTo`). Android conveniences live under
  `androidMain` and pull in `androidx.core` for FileProvider /
  MediaStore.
- **`:patcher`** — JVM-only template patcher. Uses `pdvrieze/xmlutil`
  to read existing `.docx` ZIPs, applies a `Map<String, Patch>`,
  serializes back. Depends on `:core` so patch *content* uses the
  same DSL surface.

Plus a Swift Package:

- **`swift-facade/`** — `DocxktDSL` SwiftUI-style facade over the
  Apple `XCFramework`. Covers every Kotlin DSL scope (Document,
  Paragraph, Run, Table/Row/Cell, Header/Footer, Section, Properties,
  Style, ListTemplate, Image, AnchorImage, Textbox, Footnote,
  Endnote, Comment, Hyperlink) via `@resultBuilder` types.

### XML emission

Every XML-producing class implements `appendXml(out: Appendable): Unit`
and writes its tag directly to the `Appendable`. **No intermediate
object tree.** This intentionally diverges from upstream's
`prepForXml` / `IXmlableObject` pattern — direct writes are faster,
allocate less, and let us control element/attribute order trivially.
Escaping is centralized in `io.docxkt.xml.XmlEscape`; namespace URIs
live as constants in `io.docxkt.xml.Namespaces` (single source of
truth — emitted once per OOXML part root).

Properties containers (`w:rPr`, `w:pPr`, etc.) extend
`IgnoreIfEmptyXmlComponent` so they disappear when unset — never
emit empty `<w:rPr/>` / `<w:pPr/>` tags.

### ZIP framing & codec

The ZIP framer is hand-rolled in `commonMain` (~300 LOC). DEFLATE /
INFLATE / CRC-32 come from `expect class Deflater` / `Inflater` /
`expect fun crc32` actuals — backed by `java.util.zip` on JVM /
Android and by `platform.zlib` cinterop on Apple targets, both with
`windowBits = -15` for raw RFC 1951 DEFLATE. Output is byte-identical
across every target.

### Testing

Validation is **byte-for-byte XML parity with dolanmiu/docx**.
Per-feature loop:

1. A Node.js fixture-generator script invokes upstream to produce a
   minimal `.docx` for one feature.
2. Relevant XML parts are unzipped into
   `core/src/jvmTest/resources/fixtures/<feature>/` (commonTest reads
   the same path).
3. The Kotlin equivalent is built via the `document { … }` DSL.
4. A `DocxFixtureTest` subclass diffs the generated XML against the
   fixture — XMLUnit on JVM, a hand-rolled multiplatform `XmlDiff`
   token-coalescing comparator on Apple targets. Both enforce
   `byNameAndAllAttributes + ignoreWhitespace + checkForIdentical`.

Test counts:
- `./gradlew :core:jvmTest` — 799 tests
- `./gradlew :core:macosArm64Test` — 269 tests
- `./gradlew :core:iosSimulatorArm64Test` — 269 tests
- `./gradlew :patcher:test` — 104 tests
- `cd swift-facade && swift test` — 47 tests (16 smoke + 31 facade-vs-raw fidelity)

Toolchain: Kotlin 2.3.21, JVM 21, AGP 8.13.x, `explicitApi()` enabled
in `:core` and `:patcher`. iOS 17+, macOS 14+ (arm64 only —
`macosX64` was deprecated in Kotlin 2.3.20).

## Selective-mirror policy

docxkt aims for byte-for-byte XML equality with dolanmiu/docx, but
two upstream demos (85 and 96) are explicitly conceded:

- Upstream's `paragraph-token-replacer.ts` calls `nanoid()` to
  allocate hyperlink relationship ids — we cannot byte-reproduce
  random ids without reimplementing the same RNG. Our deterministic
  `rId{N}` allocator is strictly better.
- Upstream emits orphan hyperlink rels on every `children.map`
  iteration. That is an upstream bug we deliberately do not inherit.

The patcher feature surface for hyperlinks and images in templates
ships and is tested synthetically. Only the byte-equal claim on these
two specific demos is dropped.

## License

MIT — see [LICENSE](LICENSE). docxkt is a port of
[dolanmiu/docx](https://github.com/dolanmiu/docx) (also MIT); the
upstream copyright notice is preserved in [NOTICE](NOTICE) per the
MIT license terms.

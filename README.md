# docxkt

A Kotlin library for generating Microsoft Word `.docx` files on the
JVM and Android. Port of
[dolanmiu/docx](https://github.com/dolanmiu/docx) (TypeScript) to
idiomatic Kotlin, with a DSL-first public API and an XMLUnit
golden-fixture test harness pinning the wire to upstream.

Pure Kotlin/JVM, stdlib only — no reflection, no DOM, no
intermediate object tree. Every XML-emitting class writes directly
to an `Appendable`.

## Status

**v1.0.0.**

- **86 of 95 ported demos** byte-equal upstream via XMLUnit golden
  fixtures (~95% of the `.docx` feature surface that has a
  practical real-world use). Remaining demos are niche features
  (OMML matrix operators, VML textbox visibility, rowSpan
  auto-vmerge), not common-path business-document capabilities.
- **Patcher** (read-modify-write existing `.docx` templates) ships
  with 6 of 8 upstream demos byte-equal; the remaining two (85, 96)
  are conceded by [policy](#selective-mirror-policy) — feature
  surface tested synthetically.
- All shipped: text formatting, paragraph properties, tables,
  inline images, headers/footers, sections, page numbers, footnotes,
  endnotes, comments, tracked changes, TOC, hyperlinks, bookmarks,
  symbols, lists, styles, OOXML Math (OMML), floating-image anchors,
  textboxes (VML + DrawingML), SDT form controls,
  charts/SmartArt/OLE surface, paragraph frames, the Patcher API.

## Install

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("io.github.vlaushkin:docxkt-core:1.0.0")          // pure JVM
    implementation("io.github.vlaushkin:docxkt-patcher:1.0.0")       // template patching (optional)
    implementation("io.github.vlaushkin:docxkt-android:1.0.0")       // Android helpers (optional)
}
```

Available via `mavenLocal()` after running
`./gradlew publishToMavenLocal` in the docxkt checkout, or via a
composite build:

```kotlin
// settings.gradle.kts in your project
includeBuild("../docxkt") {
    dependencySubstitution {
        substitute(module("io.github.vlaushkin:docxkt-core"))
            .using(project(":core"))
        substitute(module("io.github.vlaushkin:docxkt-patcher"))
            .using(project(":patcher"))
        substitute(module("io.github.vlaushkin:docxkt-android"))
            .using(project(":android"))
    }
}
```

## Quick start

### JVM (`:core`)

```kotlin
import io.docxkt.api.document
import io.docxkt.model.drawing.ImageFormat
import java.io.File

val logo = File("logo.png").readBytes()

val doc = document {
    paragraph {
        text("Report: ") { bold = true }
        text("Q4 2026 summary")
    }
    table {
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

### Android (`:android`)

Same `document { … }` DSL, then hand the result to the Android
helpers for MediaStore / FileProvider integration:

```kotlin
import io.docxkt.android.saveToDownloads
import io.docxkt.android.buildShareIntent

val uri: Uri = doc.saveToDownloads(
    context = this,
    displayName = "report.docx",
)

startActivity(doc.buildShareIntent(this, displayName = "report.docx"))
```

On API 29+ the file lands in the system Downloads folder via
MediaStore (no `WRITE_EXTERNAL_STORAGE` permission needed). On
older APIs it falls through to a legacy path. `buildShareIntent`
returns an `Intent.ACTION_SEND` with a FileProvider `content://`
URI — works on all supported API levels.

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
`Image`, and `DOCUMENT` (full-paragraph-list replacement) patch
types, including hyperlinks and images inside inline patches and
cross-part patching for headers/footers.

## Design

Three modules:

- **`:core`** — pure Kotlin/JVM, stdlib-only runtime deps. Public
  API is a DSL (`document { paragraph { text("…") } }`) with
  `@DslMarker` receivers. Internal state is immutable data classes
  and sealed hierarchies.
- **`:patcher`** — reads existing `.docx` ZIPs, applies a
  `Map<String, Patch>` map, serializes back. Depends on `:core` so
  patch *content* uses the same DSL surface. Runtime deps are JDK
  only (`javax.xml.parsers`, `javax.xml.stream`, `org.w3c.dom`).
- **`:android`** — thin wrapper for Android conveniences
  (`Uri`/`ContentResolver`, scoped storage on API 29+, FileProvider
  for share intents). Depends on `:core` via `api(...)` so
  consumers transitively see `:core` types.

### XML emission strategy

Every XML-producing class implements
`appendXml(out: Appendable): Unit` and writes its tag directly to
the `Appendable`. **No intermediate object tree.** This
intentionally diverges from upstream's `prepForXml` / `IXmlableObject`
pattern — direct writes are faster, allocate less, and let us
control element/attribute order trivially. Escaping is centralized
in `io.docxkt.xml.XmlEscape`; namespace prefixes (`w:`, `r:`,
`a:`, etc.) and URIs live as constants in
`io.docxkt.xml.Namespaces` (single source of truth — emitted once
per OOXML part root).

Properties containers (`w:rPr`, `w:pPr`, etc.) extend
`IgnoreIfEmptyXmlComponent` so they disappear when unset — never
emit empty `<w:rPr/>` / `<w:pPr/>` tags. Deferred-resolution slots
(images, hyperlinks, numbering references) carry placeholders that
get filled at `Document.buildDocument()` time once the relationship
allocator runs.

### Testing

The validation criterion is **byte-for-byte XML parity with
dolanmiu/docx**. Per-feature loop:

1. A Node.js fixture-generator script invokes upstream to produce
   a minimal `.docx` for one feature.
2. Relevant XML parts are unzipped into
   `core/src/test/resources/fixtures/<feature>/`.
3. The Kotlin equivalent is built via the `document { … }` DSL.
4. A `DocxFixtureTest` subclass diffs the generated XML against
   the fixture via XMLUnit (`ignoreWhitespace`,
   `byNameAndAllAttributes`, `checkForIdentical`).

XML diffing uses `xmlunit-core` + `xmlunit-matchers`. Image and
font binary parts diff via byte equality. The harness
auto-discovers compared parts from each fixture folder and admits
per-test `expectedDivergences` for legitimate
"minimum-part-output" differences from upstream's always-emit
behaviour.

`./gradlew :core:test` runs the JVM suite (~580 `@Test` + 5
`@TestFactory` methods over 231 fixture folders).
`./gradlew :android:connectedDebugAndroidTest` runs the
device/emulator suite. `./gradlew check` runs everything.

Toolchain: Kotlin 2.3.20, JVM 21, `explicitApi()` enabled in
`:core` and `:patcher`. Android: `minSdk 23`, AGP 8.13.x.

## Feature matrix

Status legend: ✅ shipped (byte-equal demo or synthetic test),
🟡 partial (some demos byte-equal, some pending).

| Feature | Status |
|---|---|
| Run formatting — bold / italic / underline / color / size / font / highlight | ✅ |
| Run extras — border, shading, measurements, language, text effects, rStyle | ✅ |
| Paragraph properties — alignment, indent, spacing, keepNext, widowControl | ✅ |
| Paragraph extras — pStyle, borders, shading, tabs, contextualSpacing | ✅ |
| Tables — rows, cells, grid, widths (dxa/pct), gridSpan, vMerge | ✅ |
| Table/cell — borders, shading, margins | ✅ |
| Sections, page size, margins, line/page breaks | ✅ |
| Headers & footers (multi-section, multi-type) | ✅ |
| Inline images (PNG / JPEG) | ✅ |
| Numbering — ordered & bullet lists, multi-level, per-list refs | ✅ |
| Styles — paragraph & character, `basedOn` chains | ✅ |
| Hyperlinks — external (URL) & internal (bookmark anchor) | ✅ |
| Bookmarks — in-paragraph & spanning | ✅ |
| Metadata — `core.xml`, `app.xml`, `custom.xml`, `settings.xml`, `fontTable.xml` | ✅ |
| Fields — simple + complex (`begin`/`instrText`/`separate`/`end`) | ✅ |
| Page numbers — `pageNumber()` / `totalPages()` / section variants | ✅ |
| Footnotes + endnotes | ✅ |
| Table of Contents — SDT-wrapped complex TOC field | ✅ |
| Comments — `<w:comments>` part with range markers | ✅ |
| Tracked changes — `<w:ins>` / `<w:del>` | ✅ |
| Symbols — `<w:sym w:char w:font>` | ✅ |
| OOXML Math (OMML) — radical, n-ary, fraction, brackets, scripts | 🟡 |
| Drawing anchor — floating images, text wrap | ✅ |
| Textbox — VML + DrawingML | 🟡 |
| SDT form controls — checkbox, date picker, dropdown | ✅ |
| Charts / SmartArt / OLE surface | ✅ |
| Paragraph frames (`<w:framePr>`) | ✅ |
| Embedded fonts (TTF obfuscation per ECMA-376) | ✅ |
| **Patcher** — `Text` / `ParagraphInline` / `Paragraphs` / `Rows` / `Image` / `DOCUMENT` | ✅ |
| Patcher — cross-part (header/footer) with per-part rels | ✅ |
| Patcher — hyperlinks & images inside inline patches | ✅ |

## Selective-mirror policy

docxkt aims for byte-for-byte XML equality with dolanmiu/docx, but
two upstream demos (85 and 96) are *explicitly conceded* under a
selective-mirror policy:

- Upstream's `paragraph-token-replacer.ts` calls `nanoid()` to
  allocate hyperlink relationship ids — we cannot byte-reproduce
  random ids without reimplementing the same RNG. Our deterministic
  `rId{N}` allocator is strictly better.
- Upstream emits orphan hyperlink rels on every `children.map`
  iteration. That is an upstream bug we deliberately do not
  inherit.

The patcher feature surface for hyperlinks and images in templates
**ships and is tested synthetically.** Only the byte-equal claim
on these two specific demos is dropped. If you need exact-rId
reproduction of an upstream-generated template, file an issue.

## License

MIT — see [LICENSE](LICENSE). docxkt is a port of
[dolanmiu/docx](https://github.com/dolanmiu/docx) (also MIT); the
upstream copyright notice is preserved in [NOTICE](NOTICE) per the
MIT license terms.

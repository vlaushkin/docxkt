# Changelog

## 1.2.0

Swift facade `DocxktDSL` reaches typography parity with the Kotlin DSL —
20 new modifiers covering paragraph layout, table styling, cell styling,
and section/document scopes, with 23 new fidelity tests proving byte-equal
output against the raw bridged Kotlin API.

- **Run modifiers (`Text`):** `.color(_)`, `.size(_)`, `.font(_)`,
  `.highlight(_)`, `.strike()`, `.doubleStrike()`, `.superScript()`,
  `.subScript()`. Chain freely with the existing `.bold()` / `.italic()`
  / `.underline()` / `.styled(_)`.
- **Paragraph layout:** `.alignment(_)`, `.spacing(_)`, `.indent(_)`,
  `.borders(_)` SwiftUI-style modifiers on `Paragraph`. Spacing /
  indentation accept either a struct or named twips parameters.
- **Table styling:** `Table.borders(_)`, `Table.shading(_)`,
  `Table.cellMargins(_)`.
- **Cell styling:** `Cell.borders(_)`, `Cell.shading(_)`, `Cell.margins(_)`,
  `Cell.verticalAlign(_)`, `Cell.gridSpan(_)`, `Cell.verticalMerge(_)` —
  full table layout coverage including colspan / rowspan equivalents.
- **Section:** `Section(pageBorders: PageBorders.all(...))` — page
  borders with `display` / `offsetFrom` / `zOrder` knobs.
- **Document:** `LineNumbering(countBy:start:distance:restart:)` as a
  top-level Document block.

New shared models: `BorderSide` + `BorderStyle` (28 styles),
`Borders` / `PageBorders`, `Spacing`, `Indentation`, `CellMargins`,
`Shading` + `ShadingPattern` (38 patterns), `Alignment` (13 cases),
`HighlightColor`, `LineRule`, `CellVerticalAlignment`, `CellVerticalMerge`,
`LineNumberRestartKind`, `PageBorderDisplay` / `OffsetFrom` / `ZOrder`.
All native Swift enums / structs that map cleanly to the bridged
Kotlin types.

No Kotlin core / patcher changes — XCFramework binary is unchanged
from v1.1.0.

Swift test count: **70** (was 47).

## 1.1.1

- `DocxktDSL.Section` now accepts `margins:` — page margins (`<w:pgMar>`)
  reach parity with the Kotlin `sectionBreak { margins(...) }` scope.
  New `Section.PageMargins` struct holds `top/right/bottom/left/header/
  footer/gutter` in twips with upstream-matching defaults
  (`1440 / 1440 / 1440 / 1440 / 708 / 708 / 0`); `.inches(...)` and
  `.cm(...)` factories build from physical units.
- No Kotlin core / patcher changes — XCFramework binary is unchanged
  from v1.1.0.

## 1.1.0

Kotlin Multiplatform release. JVM and Android consumers keep the v1 API;
iOS and macOS land via an Apple `XCFramework` and a SwiftUI-style
`DocxktDSL` Swift Package.

- `:core` migrated from JVM-only to KMP. New targets: `iosX64`,
  `iosArm64`, `iosSimulatorArm64`, `macosArm64`. JVM and Android stay on
  `java.util.zip` / `java.time`; Apple targets bind libz via Kotlin/
  Native's bundled `platform.zlib` and use `NSISO8601DateFormatter` for
  timestamps. Output is byte-identical across every target.
- 200+ fixture-driven tests now run on `:core:jvmTest`,
  `:core:macosArm64Test`, and `:core:iosSimulatorArm64Test`.
- The standalone `:android` module was folded into `:core/androidMain`.
  Gradle's variant-aware resolution picks the right artifact for JVM /
  Android consumers — no consumer-side changes needed.
- New Swift Package `swift-facade/` ships a SwiftUI-style
  `@resultBuilder` API (`DocxktDSL`) covering every Kotlin DSL scope,
  plus a unified iOS+macOS sample app at `sample-apple/`.

## 1.0.0

Initial release.

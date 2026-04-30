# Changelog

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

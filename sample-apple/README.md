# sample-apple

Unified iOS + macOS SwiftUI demo app consuming `DocxktDSL` (the Swift
facade in `../swift-facade/`) which in turn wraps the docxkt
Kotlin Multiplatform `Docxkt.xcframework`.

## Run

```bash
# 1. Build the underlying KMP XCFramework (once per docxkt update):
./gradlew :core:assembleDocxktReleaseXCFramework

# 2. Build + run the macOS sample:
xcodebuild -project sample-apple.xcodeproj \
           -scheme SampleApple \
           -destination 'platform=macOS,arch=arm64' \
           -configuration Debug \
           ARCHS=arm64 EXCLUDED_ARCHS=x86_64 \
           CODE_SIGN_IDENTITY="-" CODE_SIGNING_REQUIRED=NO \
           build
open ~/Library/Developer/Xcode/DerivedData/sample-apple-*/Build/Products/Debug/SampleApple.app

# 3. Or build for an iOS simulator:
xcodebuild -project sample-apple.xcodeproj \
           -scheme SampleApple \
           -destination 'platform=iOS Simulator,name=iPhone 17' \
           -configuration Debug \
           ARCHS=arm64 EXCLUDED_ARCHS=x86_64 \
           build

# 4. Or just open it in Xcode and hit ⌘R.
open sample-apple.xcodeproj
```

## What it demonstrates

`ContentView.swift` constructs a multi-section `.docx` through the
SwiftUI-style facade — header, body paragraphs with inline bold /
italic / underline runs, a 3-column / 3-row table with bold header
cells, and a footer:

```swift
let document = Document {
    Header { Paragraph { Text("Q1 Report").bold() } }
    Paragraph {
        Text("Revenue grew ").italic()
        Text("12%").bold()
        Text(" quarter over quarter…")
    }
    Table {
        Row {
            Cell { Paragraph { Text("Product").bold() } }
            …
        }
        …
    }
    Footer { Paragraph { Text("Confidential — internal only").italic() } }
}
try document.write(to: url)
```

On iOS the app saves the file to the app's Documents directory and
exposes a Share button that opens `UIActivityViewController`. On macOS
it writes to `~/Downloads/docxkt-sample.docx` and offers a
"Reveal in Finder" button.

## Constraints

- macOS 14+, iOS 17+ (matches the `Docxkt.xcframework` deployment
  targets).
- macOS slice is arm64 only — the XCFramework's `macos-arm64` slice
  doesn't include x86_64. `ARCHS=arm64 EXCLUDED_ARCHS=x86_64` is
  baked into the project settings but you may need to pass it on the
  command line for the iOS Simulator destination.
- `CODE_SIGN_IDENTITY="-"` skips code signing for local builds. For
  archives / TestFlight / App Store, configure a real signing
  identity in the project settings.

## Layout

```
sample-apple/
├── SampleApple/
│   ├── SampleAppleApp.swift     — @main entry
│   ├── ContentView.swift        — single screen with the Generate button
│   ├── SampleApple.entitlements — App Sandbox + downloads write access
│   └── Assets.xcassets/         — AppIcon + AccentColor placeholders
├── sample-apple.xcodeproj/
│   ├── project.pbxproj
│   ├── project.xcworkspace/
│   └── xcshareddata/xcschemes/SampleApple.xcscheme
└── README.md
```

The Swift package dependency is declared as a relative path
(`../swift-facade`) so iterating on the facade and the sample
together is a single Xcode rebuild.

// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "docxkt",
    platforms: [
        .iOS(.v13),
        .macOS(.v11),
    ],
    products: [
        .library(
            name: "DocxktDSL",
            targets: ["DocxktDSL"]
        ),
    ],
    targets: [
        .binaryTarget(
            name: "Docxkt",
            url: "https://github.com/vlaushkin/docxkt/releases/download/v1.1.0/Docxkt.xcframework.zip",
            checksum: "4465b397d942a324ca6fbd4f92686531e4a2803b802fdc52d01c3e3d47eb53f2"
        ),
        .target(
            name: "DocxktDSL",
            dependencies: ["Docxkt"],
            path: "swift-facade/Sources/DocxktDSL"
        ),
        .testTarget(
            name: "DocxktDSLTests",
            dependencies: ["DocxktDSL"],
            path: "swift-facade/Tests/DocxktDSLTests"
        ),
    ]
)

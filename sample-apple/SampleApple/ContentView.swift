import SwiftUI
import DocxktDSL

#if canImport(UIKit)
import UIKit
#elseif canImport(AppKit)
import AppKit
#endif

struct ContentView: View {
    @State private var lastSavedURL: URL?
    @State private var lastSize: Int?
    @State private var errorMessage: String?
    @State private var showShareSheet = false

    var body: some View {
        VStack(spacing: 24) {
            Text("docxkt sample")
                .font(.largeTitle)
                .fontWeight(.semibold)

            Text("Generates a multi-section .docx via the SwiftUI-style DocxktDSL.")
                .multilineTextAlignment(.center)
                .foregroundStyle(.secondary)
                .padding(.horizontal)

            Button {
                generate()
            } label: {
                Label("Generate report.docx", systemImage: "doc.text.fill")
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
            }
            .buttonStyle(.borderedProminent)

            if let size = lastSize {
                VStack(spacing: 4) {
                    Text("\(size) bytes generated")
                        .font(.subheadline.monospacedDigit())
                    if let url = lastSavedURL {
                        #if os(iOS)
                        Button("Share") { showShareSheet = true }
                            .font(.subheadline)
                        #else
                        Button("Reveal in Finder") {
                            NSWorkspace.shared.activateFileViewerSelecting([url])
                        }
                        .font(.subheadline)
                        #endif
                    }
                }
                .foregroundStyle(.secondary)
            }

            if let error = errorMessage {
                Text(error)
                    .font(.footnote)
                    .foregroundStyle(.red)
                    .multilineTextAlignment(.center)
                    .padding(.horizontal)
            }
        }
        .padding(32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        #if os(iOS)
        .sheet(isPresented: $showShareSheet) {
            if let url = lastSavedURL {
                ShareSheet(items: [url])
            }
        }
        #endif
    }

    private func generate() {
        errorMessage = nil
        let document = Document {
            Header { Paragraph { Text("Q1 Report").bold() } }
            Paragraph {
                Text("Revenue grew ").italic()
                Text("12%").bold()
                Text(" quarter over quarter — driven by international expansion and ")
                Text("rolling SKU consolidation").underline()
                Text(".")
            }
            // Width is full-page (5000 = 100% in OOXML's
            // fiftieths-of-percent unit) with three equal columns.
            // Skipping `width:` falls back to the upstream-parity
            // `auto-100-twips` default, which renders as single-character
            // columns in Pages / LibreOffice — accurate to upstream
            // dolanmiu/docx but not what real consumers want.
            Table(width: .pct(5000), columnWidths: [3000, 3000, 3000]) {
                Row {
                    Cell { Paragraph { Text("Product").bold() } }
                    Cell { Paragraph { Text("Units").bold() } }
                    Cell { Paragraph { Text("Revenue").bold() } }
                }
                Row {
                    Cell { Paragraph { Text("Widgets") } }
                    Cell { Paragraph { Text("1,420") } }
                    Cell { Paragraph { Text("$184,600") } }
                }
                Row {
                    Cell { Paragraph { Text("Gadgets") } }
                    Cell { Paragraph { Text("3,150") } }
                    Cell { Paragraph { Text("$472,500") } }
                }
            }
            Footer { Paragraph { Text("Confidential — internal only").italic() } }
        }
        do {
            let url = try targetURL()
            try document.write(to: url)
            lastSavedURL = url
            lastSize = document.toData().count
        } catch {
            errorMessage = "Save failed: \(error.localizedDescription)"
        }
    }

    private func targetURL() throws -> URL {
        #if os(iOS)
        let dir = try FileManager.default.url(
            for: .documentDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: true,
        )
        return dir.appendingPathComponent("report.docx")
        #else
        let downloads = try FileManager.default.url(
            for: .downloadsDirectory,
            in: .userDomainMask,
            appropriateFor: nil,
            create: false,
        )
        return downloads.appendingPathComponent("docxkt-sample.docx")
        #endif
    }
}

#if os(iOS)
struct ShareSheet: UIViewControllerRepresentable {
    let items: [Any]
    func makeUIViewController(context: Context) -> UIActivityViewController {
        UIActivityViewController(activityItems: items, applicationActivities: nil)
    }
    func updateUIViewController(_ uiViewController: UIActivityViewController, context: Context) {}
}
#endif

#Preview {
    ContentView()
}

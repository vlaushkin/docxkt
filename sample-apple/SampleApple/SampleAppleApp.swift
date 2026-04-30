import SwiftUI

@main
struct SampleAppleApp: App {
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        #if os(macOS)
        .defaultSize(width: 480, height: 360)
        #endif
    }
}

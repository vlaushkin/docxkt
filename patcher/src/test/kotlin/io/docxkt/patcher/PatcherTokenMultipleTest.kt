package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherTokenMultipleTest : PatcherFixtureTest("patcher-token-multiple") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.Text("Alice"),
        "company" to Patch.Text("ACME"),
        "role" to Patch.Text("Engineer"),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

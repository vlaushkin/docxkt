package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherTokenSpanningRunsTest : PatcherFixtureTest("patcher-token-spanning-runs") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.Text("Alice"),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

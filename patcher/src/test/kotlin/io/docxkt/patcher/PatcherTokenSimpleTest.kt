package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherTokenSimpleTest : PatcherFixtureTest("patcher-token-simple") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.Text("Alice"),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

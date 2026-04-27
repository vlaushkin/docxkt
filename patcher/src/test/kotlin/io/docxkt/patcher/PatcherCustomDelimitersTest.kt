package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherCustomDelimitersTest : PatcherFixtureTest("patcher-custom-delimiters") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.Text("Alice"),
    )

    override val placeholderDelimiters: Pair<String, String> = "<%" to "%>"

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

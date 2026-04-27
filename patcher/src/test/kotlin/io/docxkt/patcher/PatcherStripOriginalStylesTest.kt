package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherStripOriginalStylesTest : PatcherFixtureTest("patcher-strip-original-styles") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.Text("Alice"),
    )

    override val keepOriginalStyles: Boolean = false

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

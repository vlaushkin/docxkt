package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherNonRecursiveTest : PatcherFixtureTest("patcher-non-recursive") {

    override fun patches(): Map<String, Patch> = mapOf(
        "key" to Patch.Text("VALUE"),
    )

    override val recursive: Boolean = false

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

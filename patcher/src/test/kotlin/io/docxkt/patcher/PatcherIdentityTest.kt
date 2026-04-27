package io.docxkt.patcher

import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherIdentityTest : PatcherFixtureTest("patcher-identity") {

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
        "[Content_Types].xml",
        "_rels/.rels",
    )
}

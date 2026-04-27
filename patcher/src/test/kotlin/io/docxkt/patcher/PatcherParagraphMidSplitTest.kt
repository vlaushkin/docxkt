package io.docxkt.patcher

import io.docxkt.api.paragraphs
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherParagraphMidSplitTest : PatcherFixtureTest("patcher-paragraph-mid-split") {

    override fun patches(): Map<String, Patch> = mapOf(
        "block" to Patch.Paragraphs(
            paragraphs {
                paragraph { text("INSERTED") }
            }
        ),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt.patcher

import io.docxkt.api.paragraphs
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherParagraphReplaceLineTest : PatcherFixtureTest("patcher-paragraph-replace-line") {

    override fun patches(): Map<String, Patch> = mapOf(
        "block" to Patch.Paragraphs(
            paragraphs {
                paragraph { text("Inserted A") }
                paragraph { text("Inserted B") }
            }
        ),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

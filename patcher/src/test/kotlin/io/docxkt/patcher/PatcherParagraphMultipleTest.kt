package io.docxkt.patcher

import io.docxkt.api.paragraphs
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherParagraphMultipleTest : PatcherFixtureTest("patcher-paragraph-multiple") {

    override fun patches(): Map<String, Patch> = mapOf(
        "first" to Patch.Paragraphs(
            paragraphs {
                paragraph { text("F1") }
                paragraph { text("F2") }
            }
        ),
        "second" to Patch.Paragraphs(
            paragraphs {
                paragraph { text("S1") }
            }
        ),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

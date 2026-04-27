package io.docxkt.patcher

import io.docxkt.api.tableRows
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherRowsInjectTest : PatcherFixtureTest("patcher-rows-inject") {

    override fun patches(): Map<String, Patch> = mapOf(
        "rows" to Patch.Rows(
            tableRows {
                row { cell { paragraph { text("Alice") } } }
                row { cell { paragraph { text("Bob") } } }
            }
        ),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

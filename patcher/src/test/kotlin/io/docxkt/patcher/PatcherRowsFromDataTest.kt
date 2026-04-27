package io.docxkt.patcher

import io.docxkt.api.tableRows
import io.docxkt.patcher.testing.PatcherFixtureTest

internal class PatcherRowsFromDataTest : PatcherFixtureTest("patcher-rows-from-data") {

    override fun patches(): Map<String, Patch> {
        val data = listOf("Alice" to "95", "Bob" to "82", "Carol" to "78")
        return mapOf(
            "records" to Patch.Rows(
                tableRows {
                    for ((name, score) in data) {
                        row {
                            cell { paragraph { text(name) } }
                            cell { paragraph { text(score) } }
                        }
                    }
                }
            ),
        )
    }

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )
}

package io.docxkt.patcher

import io.docxkt.api.runs
import io.docxkt.patcher.testing.PatcherFixtureTest

/**
 * Port of upstream demo-87 (template-document).
 *
 * Single `{{name}}` marker in `simple-template-2.docx`, replaced
 * with a plain `TextRun("Max")` via `PatchType.PARAGRAPH`. Our
 * equivalent is `Patch.ParagraphInline` with one unformatted run.
 */
internal class PatcherDemo87Test : PatcherFixtureTest("patcher-demo-87") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.ParagraphInline(runs { run("Max") }),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )

    override val inputParts: List<String> = listOf(
        "[Content_Types].xml",
        "_rels/.rels",
        "docProps/app.xml",
        "docProps/core.xml",
        "word/_rels/document.xml.rels",
        "word/document.xml",
        "word/endnotes.xml",
        "word/fontTable.xml",
        "word/footnotes.xml",
        "word/settings.xml",
        "word/styles.xml",
        "word/theme/theme1.xml",
        "word/webSettings.xml",
    )
}

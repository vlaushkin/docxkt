package io.docxkt.patcher

import io.docxkt.api.runs
import io.docxkt.patcher.testing.PatcherFixtureTest

/**
 * Port of upstream demo-89 (template-document).
 *
 * Two markers in `simple-template-3.docx`:
 *  - `{{salutation}}` → "Mr." with font Trebuchet MS
 *  - `{{first-name}}` → "John" with font Trebuchet MS
 *
 * Exercises both single-run markers (each occurrence in p[1])
 * and multi-run markers (the `{{salut`+`at`+`ion}}` split in p[12]).
 * `keepOriginalStyles: true` (default).
 */
internal class PatcherDemo89Test : PatcherFixtureTest("patcher-demo-89") {

    override fun patches(): Map<String, Patch> = mapOf(
        "salutation" to Patch.ParagraphInline(
            runs { run("Mr.") { font("Trebuchet MS") } }
        ),
        "first-name" to Patch.ParagraphInline(
            runs { run("John") { font("Trebuchet MS") } }
        ),
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
        "word/numbering.xml",
        "word/settings.xml",
        "word/styles.xml",
        "word/theme/theme1.xml",
        "word/webSettings.xml",
    )
}

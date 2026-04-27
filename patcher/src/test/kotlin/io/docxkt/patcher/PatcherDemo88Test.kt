package io.docxkt.patcher

import io.docxkt.api.runs
import io.docxkt.patcher.testing.PatcherFixtureTest

/**
 * Port of upstream demo-88 (template-document).
 *
 * Four ParagraphInline patches in `simple-template.docx`:
 *  - `{{name}}` → "Mr" (Trebuchet MS)
 *  - `{{paragraph_replace}}` → "Lorem ipsum paragraph" (Trebuchet MS)
 *  - `{{table_heading_1}}` → "John" (Trebuchet MS)
 *  - `{{item_1}}` → "Doe" (Trebuchet MS)
 *
 * `{{table}}` and `{{image_test}}` markers in the input are NOT
 * patched (left in the output verbatim).
 */
internal class PatcherDemo88Test : PatcherFixtureTest("patcher-demo-88") {

    override fun patches(): Map<String, Patch> = mapOf(
        "name" to Patch.ParagraphInline(
            runs { run("Mr") { font("Trebuchet MS") } }
        ),
        "paragraph_replace" to Patch.ParagraphInline(
            runs { run("Lorem ipsum paragraph") { font("Trebuchet MS") } }
        ),
        "table_heading_1" to Patch.ParagraphInline(
            runs { run("John") { font("Trebuchet MS") } }
        ),
        "item_1" to Patch.ParagraphInline(
            runs { run("Doe") { font("Trebuchet MS") } }
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
        "word/footer1.xml",
        "word/footnotes.xml",
        "word/header1.xml",
        "word/settings.xml",
        "word/styles.xml",
        "word/theme/theme1.xml",
        "word/webSettings.xml",
    )
}

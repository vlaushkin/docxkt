package io.docxkt.patcher

import io.docxkt.api.runs
import io.docxkt.patcher.testing.PatcherFixtureTest

/**
 * Port of upstream demo-93 (template-document field-trip).
 *
 * Twelve simple ParagraphInline patches, all with plain text runs
 * (no font). Every marker is heavily fragmented (4-5 source runs
 * each), so this exercises the multi-span path. The original demo
 * uses `new Date().toLocaleDateString()` for `todays_date`; the
 * fixture pinned the date the day it was generated, so the test
 * uses that pinned value.
 */
internal class PatcherDemo93Test : PatcherFixtureTest("patcher-demo-93") {

    override fun patches(): Map<String, Patch> = mapOf(
        "todays_date" to Patch.ParagraphInline(runs { run("4/27/2026") }),
        "school_name" to Patch.ParagraphInline(runs { run("test") }),
        "address" to Patch.ParagraphInline(runs { run("blah blah") }),
        "city" to Patch.ParagraphInline(runs { run("test") }),
        "state" to Patch.ParagraphInline(runs { run("test") }),
        "zip" to Patch.ParagraphInline(runs { run("test") }),
        "phone" to Patch.ParagraphInline(runs { run("test") }),
        "first_name" to Patch.ParagraphInline(runs { run("test") }),
        "last_name" to Patch.ParagraphInline(runs { run("test") }),
        "email_address" to Patch.ParagraphInline(runs { run("test") }),
        "ft_dates" to Patch.ParagraphInline(runs { run("test") }),
        "grade" to Patch.ParagraphInline(runs { run("test") }),
    )

    override val comparedParts: List<String> = listOf(
        "word/document.xml",
    )

    override val inputParts: List<String> = listOf(
        "[Content_Types].xml",
        "_rels/.rels",
        "docProps/app.xml",
        "docProps/core.xml",
        "docProps/custom.xml",
        "word/_rels/document.xml.rels",
        "word/_rels/footer1.xml.rels",
        "word/document.xml",
        "word/fontTable.xml",
        "word/footer1.xml",
        "word/header1.xml",
        "word/numbering.xml",
        "word/settings.xml",
        "word/styles.xml",
        "word/theme/theme1.xml",
    )

    override val inputBinaryParts: List<String> = listOf(
        "word/media/image1.png",
    )
}

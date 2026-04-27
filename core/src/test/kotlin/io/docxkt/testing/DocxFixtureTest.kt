// Testing helper — no upstream analogue. Upstream's tests assert on an
// intermediate object tree we don't produce; we compare emitted XML to
// golden fixtures via XMLUnit instead.
package io.docxkt.testing

import io.docxkt.api.Document
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory

/**
 * Base class for every fixture-backed regression test.
 *
 * A subclass supplies the [fixtureName] (folder under
 * `core/src/test/resources/fixtures/`), builds a [Document] via our
 * DSL in [build], and lists the parts it wants diffed in
 * [comparedParts] (XML, via XMLUnit) and [comparedBinaryParts] (byte
 * equality, for images and other binary payloads).
 *
 * We use `@TestFactory` so each compared part shows up as its own
 * test node in reports — a multi-part fixture with one failing part
 * still tells you exactly which part failed.
 */
internal abstract class DocxFixtureTest(
    private val fixtureName: String,
) {
    /** Build the document to serialize and compare against the fixture. */
    protected abstract fun build(): Document

    /**
     * XML parts inside the generated `.docx` to diff against the
     * fixture via XMLUnit.
     *
     * Default `emptyList()` triggers auto-discovery: every `.xml`
     * and `.rels` file present in the fixture folder is compared,
     * minus any keys present in [expectedDivergences]. Subclasses
     * can still override with an explicit list when they want
     * narrower comparison (e.g. legacy fixtures with strict
     * per-part scope).
     */
    protected open val comparedParts: List<String> = emptyList()

    /**
     * Parts the generated output is *allowed* to diverge on. Each
     * entry pairs a part path with a one-line rationale.
     *
     * Subclass values are MERGED with `defaultDivergences` (the
     * global minimum-part-output set) at the use site, so per-test
     * overrides only need to list their extras. Auto-discovered
     * comparedParts skip the merged set; explicit `comparedParts`
     * lists are unaffected.
     */
    protected open val expectedDivergences: Map<String, String> = emptyMap()

    private val effectiveDivergences: Map<String, String>
        get() = defaultDivergences + expectedDivergences

    internal companion object {
        /**
         * Global "minimum-part output" rationale. The DSL emits a
         * part only when it has user-supplied content; upstream
         * always ships these even when empty.
         */
        val defaultDivergences: Map<String, String> = mapOf(
            "[Content_Types].xml" to
                "derived from which parts/content the doc has — minimum-part design.",
            "_rels/.rels" to
                "derived from which package-level rels exist — minimum-part design.",
            "word/_rels/document.xml.rels" to
                "derived from which document-level rels exist — minimum-part design.",
            "docProps/core.xml" to
                "dcterms:created / dcterms:modified default to Instant.now(); fixture has sentinel.",
            "docProps/custom.xml" to
                "ours: emit only when DSL registers customProperties.",
            "word/comments.xml" to
                "ours: emit only when DSL registers comments.",
            "word/endnotes.xml" to
                "ours: emit only when DSL registers endnotes.",
            "word/footnotes.xml" to
                "ours: emit only when DSL registers footnotes.",
            "word/numbering.xml" to
                "ours: emit only when DSL registers list templates.",
            "word/styles.xml" to
                "ours: emit only when DSL registers styles.",
            "word/_rels/comments.xml.rels" to
                "ours: emitted with comments.xml only.",
            "word/_rels/endnotes.xml.rels" to
                "ours: emitted with endnotes.xml only.",
            "word/_rels/footnotes.xml.rels" to
                "ours: emitted with footnotes.xml only.",
            "word/_rels/fontTable.xml.rels" to
                "ours: fontTable rels emitted only when fonts are embedded.",
        ) + (1..6).associate { n ->
            "word/_rels/header$n.xml.rels" to
                "ours: header$n rels emitted only when header content has rIds (image/hyperlink/etc.)."
        } + (1..6).associate { n ->
            "word/_rels/footer$n.xml.rels" to
                "ours: footer$n rels emitted only when footer content has rIds (image/hyperlink/etc.)."
        }
    }

    /**
     * Binary parts inside the generated `.docx` to compare via byte
     * equality (images, embedded fonts, etc.). Default `emptyList()`
     * so text-only fixtures don't need to override.
     */
    protected open val comparedBinaryParts: List<String> = emptyList()

    @TestFactory
    fun `matches golden fixture`(): List<DynamicTest> {
        val actualBytes = build().toByteArray()
        dumpIfRequested(actualBytes)
        val actualTextEntries = zipEntriesAsText(actualBytes)
        val actualBinaryEntries = zipEntriesAsBytes(actualBytes)

        val divergences = effectiveDivergences
        val effectiveComparedParts = when {
            comparedParts.isNotEmpty() -> comparedParts
            else -> FixtureLoader.discoverParts(fixtureName)
                .filter { it !in divergences }
        }
        val expectedXmlParts = FixtureLoader.loadParts(fixtureName, effectiveComparedParts)

        val xmlTests = effectiveComparedParts.map { part ->
            DynamicTest.dynamicTest("$fixtureName :: $part") {
                val actual = actualTextEntries[part]
                    ?: error("generated .docx is missing expected part: $part")
                val expected = expectedXmlParts[part]
                    ?: error("fixture is missing expected part: $part")
                assertXmlEquals(actual, expected, context = "$fixtureName/$part")
            }
        }
        val binaryTests = comparedBinaryParts.map { part ->
            DynamicTest.dynamicTest("$fixtureName :: $part (binary)") {
                val actual = actualBinaryEntries[part]
                    ?: error("generated .docx is missing expected binary part: $part")
                val expected = FixtureLoader.loadBinaryPart(fixtureName, part)
                assertBytesEqual(actual, expected, context = "$fixtureName/$part")
            }
        }
        return xmlTests + binaryTests
    }

    /**
     * If the environment variable `DOCXKT_DUMP_DIR` is set, write this
     * fixture's generated `.docx` to `$DOCXKT_DUMP_DIR/<fixtureName>.docx`.
     * No-op otherwise. Used by humans to batch-convert generated
     * documents through LibreOffice / Word for spot-checks that the
     * packager output is accepted by real consumers, not just XMLUnit.
     */
    private fun dumpIfRequested(bytes: ByteArray) {
        val dir = System.getenv("DOCXKT_DUMP_DIR") ?: return
        val out = java.io.File(dir, "$fixtureName.docx")
        out.parentFile?.mkdirs()
        out.writeBytes(bytes)
    }
}

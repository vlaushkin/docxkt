// Testing helper — no upstream analogue. Mirrors :core's
// DocxFixtureTest but for the patcher's input → patch → output
// data flow.
package io.docxkt.patcher.testing

import io.docxkt.pack.DocxPackager
import io.docxkt.patcher.Patch
import io.docxkt.patcher.PatchDocument
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * Base class for every patcher fixture-backed regression test.
 *
 * A subclass supplies the [fixtureName] (folder under
 * `patcher/src/test/resources/fixtures/`), an optional patch map
 * via [patches], and a list of compared parts via [comparedParts].
 *
 * Fixture layout:
 * - `fixtures/<name>/input/` — the source `.docx` parts (same
 *   layout `:core` fixtures use, but rooted under `input/`).
 * - `fixtures/<name>/output/` — the expected post-patch parts. For
 *   identity fixtures this is the same content as `input/`.
 *
 * The harness packs `input/` into a `.docx` byte array via
 * [DocxPackager], runs [PatchDocument.patch], unpacks the result,
 * and diffs each compared part against the matching `output/`
 * resource via XMLUnit (or byte equality for binary parts).
 */
internal abstract class PatcherFixtureTest(
    private val fixtureName: String,
) {
    /** Patches to apply. Empty default = identity (input → output unchanged). */
    protected open fun patches(): Map<String, Patch> = emptyMap()

    /** XML parts inside the patched `.docx` to diff against `output/`. */
    protected abstract val comparedParts: List<String>

    /** Binary parts to compare via byte equality. */
    protected open val comparedBinaryParts: List<String> = emptyList()

    /**
     * Override when the input parts diverge from the compared
     * parts — e.g. the image fixture's input doesn't have the
     * media binary that the patcher creates as output.
     *
     * Default: compared XML parts + compared binary parts.
     */
    protected open val inputParts: List<String>
        get() = (comparedParts + comparedBinaryParts).distinct()

    protected open val inputBinaryParts: List<String> = emptyList()

    /** Options forwarded to [PatchDocument.patch]. */
    protected open val keepOriginalStyles: Boolean = true
    protected open val placeholderDelimiters: Pair<String, String> = "{{" to "}}"
    protected open val recursive: Boolean = true

    @TestFactory
    fun `matches golden output`(): List<DynamicTest> {
        val inputBytes = packInputAsDocx()
        val outputBytes = PatchDocument.patch(
            data = inputBytes,
            patches = patches(),
            keepOriginalStyles = keepOriginalStyles,
            placeholderDelimiters = placeholderDelimiters,
            recursive = recursive,
        )
        val outputEntries = unzip(outputBytes)

        val xmlTests = comparedParts.map { part ->
            DynamicTest.dynamicTest("$fixtureName :: $part") {
                val actual = outputEntries[part]?.toString(Charsets.UTF_8)
                    ?: error("patched .docx is missing expected part: $part")
                val expected = loadOutputPart(part)
                assertXmlEquals(actual, expected, context = "$fixtureName/$part")
            }
        }
        val binaryTests = comparedBinaryParts.map { part ->
            DynamicTest.dynamicTest("$fixtureName :: $part (binary)") {
                val actual = outputEntries[part]
                    ?: error("patched .docx is missing expected binary part: $part")
                val expected = loadOutputBinaryPart(part)
                assertBytesEqual(actual, expected, context = "$fixtureName/$part")
            }
        }
        return xmlTests + binaryTests
    }

    private fun packInputAsDocx(): ByteArray {
        // Walk every input part and pack it. We don't have a
        // directory listing API for classpath resources, so we
        // accept [inputParts] + [inputBinaryParts] as the
        // canonical list. By default, [inputParts] mirrors the
        // compared parts; fixtures whose input differs from
        // their compared parts (e.g. images created by patches)
        // override.
        val parts = (inputParts + inputBinaryParts).distinct().map { partPath ->
            val bytes = loadInputBinaryPart(partPath)
            DocxPackager.Entry(path = partPath, bytes = bytes)
        }
        return DocxPackager.toByteArray(parts)
    }

    private fun loadInputBinaryPart(partPath: String): ByteArray =
        loadResource("/fixtures/$fixtureName/input/$partPath")

    private fun loadOutputPart(partPath: String): String =
        loadOutputBinaryPart(partPath).toString(Charsets.UTF_8)

    private fun loadOutputBinaryPart(partPath: String): ByteArray =
        loadResource("/fixtures/$fixtureName/output/$partPath")

    private fun loadResource(path: String): ByteArray {
        val stream = javaClass.getResourceAsStream(path)
            ?: error("Missing fixture resource: $path")
        return stream.use { it.readAllBytes() }
    }

    private fun unzip(bytes: ByteArray): Map<String, ByteArray> {
        val result = LinkedHashMap<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zin ->
            while (true) {
                val entry = zin.nextEntry ?: break
                if (!entry.isDirectory) {
                    result[entry.name] = zin.readAllBytes()
                }
                zin.closeEntry()
            }
        }
        return result
    }
}

// Testing helper — no upstream analogue.
package io.docxkt.testing

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * Loads fixture parts from `core/src/test/resources/fixtures/<name>/`.
 *
 * Part names are virtual paths matching what our packager emits: e.g.
 * `word/document.xml`, `_rels/.rels`, `[Content_Types].xml`. The
 * first two map to on-disk subdirectories; the third is a literal
 * filename — the `[` and `]` are not path separators on any platform
 * we target.
 */
internal object FixtureLoader {

    /** Load a single XML part from a fixture. Returns the raw XML as a String. */
    fun loadPart(fixtureName: String, partPath: String): String {
        val bytes = loadBinaryPart(fixtureName, partPath)
        return bytes.toString(Charsets.UTF_8)
    }

    /** Load multiple XML parts at once. */
    fun loadParts(fixtureName: String, partPaths: List<String>): Map<String, String> =
        partPaths.associateWith { loadPart(fixtureName, it) }

    /**
     * Load a single part from a fixture as raw bytes — the right call
     * for images and other binary payloads that shouldn't be
     * decoded as UTF-8.
     */
    fun loadBinaryPart(fixtureName: String, partPath: String): ByteArray {
        val resourcePath = "/fixtures/$fixtureName/$partPath"
        val stream = FixtureLoader::class.java.getResourceAsStream(resourcePath)
            ?: error("Missing fixture resource: $resourcePath")
        return stream.use { it.readAllBytes() }
    }

    /**
     * List every `.xml` or `.rels` part present in the fixture folder.
     * Used by [DocxFixtureTest]'s auto-discovery default for
     * `comparedParts`.
     *
     * Excludes auxiliary files: README.md, upstream-demo.ts, any
     * .txt (lorem-paragraph resources etc.), and word/media/
     * binaries.
     */
    fun discoverParts(fixtureName: String): List<String> {
        val rootUrl = FixtureLoader::class.java.getResource("/fixtures/$fixtureName")
            ?: error("Missing fixture root: /fixtures/$fixtureName")
        val rootPath = java.nio.file.Paths.get(rootUrl.toURI())
        val parts = mutableListOf<String>()
        java.nio.file.Files.walk(rootPath).use { stream ->
            stream.filter { java.nio.file.Files.isRegularFile(it) }.forEach { p ->
                val rel = rootPath.relativize(p).toString().replace('\\', '/')
                if (rel.endsWith(".xml") || rel.endsWith(".rels")) {
                    parts += rel
                }
            }
        }
        return parts.sorted()
    }
}

/**
 * Read all entries of a `.docx` ZIP byte array into a `path -> XML`
 * map. Decodes entries as UTF-8 — callers asserting on binary
 * payloads should use [zipEntriesAsBytes] instead.
 */
internal fun zipEntriesAsText(bytes: ByteArray): Map<String, String> =
    zipEntriesAsBytes(bytes).mapValues { (_, v) -> v.toString(Charsets.UTF_8) }

/** Read all entries of a `.docx` ZIP byte array into a `path -> bytes` map. */
internal fun zipEntriesAsBytes(bytes: ByteArray): Map<String, ByteArray> {
    val result = LinkedHashMap<String, ByteArray>()
    ZipInputStream(ByteArrayInputStream(bytes)).use { zin ->
        while (true) {
            val entry = zin.nextEntry ?: break
            result[entry.name] = zin.readAllBytes()
            zin.closeEntry()
        }
    }
    return result
}

// No upstream analogue — JSZip handles ZIP reading on the upstream
// side; we use java.util.zip.ZipInputStream directly.
package io.docxkt.patcher.io

import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream

/**
 * Reads the entries of a `.docx` (ZIP container) into a path → bytes
 * map preserving source order. Used by [io.docxkt.patcher.PatchDocument]
 * to obtain the parts of a template before parsing.
 *
 * Returns a [LinkedHashMap] so iteration order matches the order the
 * entries appeared in the source ZIP — important when re-zipping the
 * patched output, since some consumers care about entry order.
 */
internal object DocxReader {

    /**
     * Read every entry of [bytes] (a `.docx` byte array) into a
     * `path → content` map.
     *
     * Throws [IllegalArgumentException] if [bytes] is not a valid
     * ZIP archive.
     */
    internal fun read(bytes: ByteArray): Map<String, ByteArray> {
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

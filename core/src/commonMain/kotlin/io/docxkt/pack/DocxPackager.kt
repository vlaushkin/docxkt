// Port of: src/export/packer/next-compiler.ts (overall shape of the ZIP writer).
// dolanmiu uses JSZip; we use platform-native ZIP machinery (java.util.zip on
// JVM/Android, libz cinterop on Apple Native).
package io.docxkt.pack

/**
 * Writes a list of `(path, bytes)` parts into a `.docx` ZIP container.
 *
 * No OOXML knowledge — generic packager. Maintains stable entry order per
 * ZIP packaging order: package-level rels, then `[Content_Types].xml`, then
 * `word/_rels/...`, then other `word/...`, then `docProps/...`, then media.
 * Resulting `.docx` is byte-reproducible across runs for the same input.
 *
 * Public because the `:patcher` sibling module also packs entries on the
 * write side of its read-modify-write loop. Keep the surface minimal.
 *
 * The `pack(...)` and `toByteArray(...)` operations require a platform-
 * specific ZIP encoder and live in per-target source sets.
 */
public object DocxPackager {

    /** A single ZIP entry awaiting packaging. */
    public data class Entry(val path: String, val bytes: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Entry) return false
            return path == other.path && bytes.contentEquals(other.bytes)
        }

        override fun hashCode(): Int = 31 * path.hashCode() + bytes.contentHashCode()
    }

    /** 1980-01-01 00:00:00 UTC. Minimum representable in ZIP's DOS time. */
    internal const val FIXED_TIMESTAMP_MS: Long = 315532800000L

    /** Sort key: smaller bucket = earlier in the ZIP. Ties broken by path. */
    internal object EntryOrder : Comparator<Entry> {
        override fun compare(a: Entry, b: Entry): Int {
            val bucketCmp = bucket(a.path).compareTo(bucket(b.path))
            return if (bucketCmp != 0) bucketCmp else a.path.compareTo(b.path)
        }

        private fun bucket(path: String): Int = when {
            path == "_rels/.rels" -> 0
            path == "[Content_Types].xml" -> 1
            path.startsWith("word/_rels/") -> 2
            path.startsWith("word/media/") -> 5
            path.startsWith("word/") -> 3
            path.startsWith("docProps/") -> 4
            else -> 6
        }
    }
}

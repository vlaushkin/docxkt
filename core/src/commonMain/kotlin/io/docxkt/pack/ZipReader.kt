// No upstream analogue. Read side of the deterministic ZIP container —
// mirrors the writer in ZipFramer.kt. INFLATE comes from the per-platform
// `Inflater` actual (java.util.zip on JVM/Android, libz with
// windowBits = -15 on Apple).
package io.docxkt.pack

private const val LFH_SIGNATURE: Int = 0x04034b50.toInt()
private const val EOCD_SIGNATURE: Int = 0x06054b50.toInt()

private const val METHOD_STORED: Int = 0
private const val METHOD_DEFLATE: Int = 8

/** Read every entry of a ZIP byte array into a `path → bytes` map. */
public object ZipReader {

    /**
     * Parse [bytes] as a ZIP archive and return a map from entry path to
     * uncompressed content. Iteration order matches the source ZIP's
     * local-file-header order (preserved via `LinkedHashMap`).
     *
     * Supports STORED (method 0) and DEFLATE (method 8) entries — the
     * only methods OOXML packages use in practice. Other methods throw
     * `IllegalArgumentException`.
     *
     * Directory entries (paths ending in `/`) are skipped.
     */
    public fun read(bytes: ByteArray): Map<String, ByteArray> {
        val result = LinkedHashMap<String, ByteArray>()
        val inflater = Inflater()
        var offset = 0
        while (offset + 30 <= bytes.size) {
            val sig = readIntLe(bytes, offset)
            if (sig != LFH_SIGNATURE) break
            val method = readShortLe(bytes, offset + 8)
            val compSize = readIntLe(bytes, offset + 18)
            val uncompSize = readIntLe(bytes, offset + 22)
            val nameLen = readShortLe(bytes, offset + 26)
            val extraLen = readShortLe(bytes, offset + 28)

            val nameStart = offset + 30
            val nameEnd = nameStart + nameLen
            require(nameEnd <= bytes.size) { "ZIP entry name extends past buffer at offset $offset" }
            val name = bytes.decodeToString(nameStart, nameEnd)

            val dataStart = nameEnd + extraLen
            val dataEnd = dataStart + compSize
            require(dataEnd <= bytes.size) { "ZIP entry data extends past buffer at offset $offset" }

            if (!name.endsWith("/")) {
                val data = when (method) {
                    METHOD_STORED -> bytes.copyOfRange(dataStart, dataEnd)
                    METHOD_DEFLATE -> {
                        val compressed = bytes.copyOfRange(dataStart, dataEnd)
                        val inflated = inflater.inflate(compressed)
                        require(inflated.size == uncompSize) {
                            "INFLATE size mismatch for '$name': expected $uncompSize, got ${inflated.size}"
                        }
                        inflated
                    }
                    else -> error("Unsupported ZIP compression method $method for entry '$name'")
                }
                result[name] = data
            }

            offset = dataEnd
            // Stop reading once we hit the central directory (signature
            // `0x02014b50`) or end-of-central-directory (`0x06054b50`).
            if (offset + 4 <= bytes.size) {
                val nextSig = readIntLe(bytes, offset)
                if (nextSig == EOCD_SIGNATURE || nextSig != LFH_SIGNATURE) break
            }
        }
        return result
    }

    private fun readIntLe(bytes: ByteArray, off: Int): Int =
        (bytes[off].toInt() and 0xff) or
            ((bytes[off + 1].toInt() and 0xff) shl 8) or
            ((bytes[off + 2].toInt() and 0xff) shl 16) or
            ((bytes[off + 3].toInt() and 0xff) shl 24)

    private fun readShortLe(bytes: ByteArray, off: Int): Int =
        (bytes[off].toInt() and 0xff) or
            ((bytes[off + 1].toInt() and 0xff) shl 8)
}

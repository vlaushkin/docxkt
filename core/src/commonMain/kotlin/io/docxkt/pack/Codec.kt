// No upstream analogue. dolanmiu/docx delegates ZIP+DEFLATE to JSZip; we
// hand-roll the ZIP framer (see ZipFramer.kt) and need a per-platform
// codec for raw DEFLATE bytes and CRC-32. The output of
// `Deflater().deflate(input)` MUST be byte-identical to JDK's
// `java.util.zip.Deflater(level = DEFAULT_COMPRESSION, nowrap = true)`
// — that's the contract the existing fixture diffs rely on.
package io.docxkt.pack

/**
 * Streaming RFC 1951 raw DEFLATE encoder.
 *
 * `nowrap = true` semantics: no zlib (RFC 1950) or gzip (RFC 1952)
 * wrapper, no Adler-32 trailer. ZIP file format requires raw DEFLATE
 * exactly, and JDK's `java.util.zip.Deflater(nowrap = true)` is the
 * reference output.
 *
 * On Apple platforms the actual binds libz with `windowBits = -15`
 * (negative window bits flag for "raw deflate"). Apple's Compression
 * framework's `COMPRESSION_ZLIB` writes RFC 1950 with an Adler-32
 * trailer and is intentionally NOT used.
 */
internal expect class Deflater() {
    /**
     * Encode [input] in one shot at the JDK default compression level
     * (`Z_DEFAULT_COMPRESSION = -1`, ≈ level 6) with strategy
     * `Z_DEFAULT_STRATEGY = 0`. The returned bytes are the raw DEFLATE
     * stream (no header, no trailer) terminated with the
     * `BFINAL = 1` bit so a single call produces a complete stream.
     */
    fun deflate(input: ByteArray): ByteArray
}

/**
 * Streaming RFC 1951 raw DEFLATE decoder. Inverse of [Deflater] —
 * `nowrap = true` on JVM, `windowBits = -15` on Apple. Used by
 * `:patcher` callers that read existing `.docx` parts and by tests
 * that round-trip-verify framer output.
 */
internal expect class Inflater() {
    /**
     * Decode [input] in one shot; the input is expected to terminate
     * the stream (`Z_STREAM_END`). Throws on truncated or malformed
     * data.
     */
    fun inflate(input: ByteArray): ByteArray
}

/**
 * CRC-32/ISO-HDLC checksum over [bytes]. Matches
 * `java.util.zip.CRC32` (polynomial 0xEDB88320, reflected, init = 0,
 * xorout = 0xFFFFFFFF). Used in ZIP local-file-header and
 * central-directory CRC fields.
 */
internal expect fun crc32(bytes: ByteArray): Long

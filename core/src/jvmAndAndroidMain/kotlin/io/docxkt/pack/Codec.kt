// No upstream analogue. JDK-side actuals for the codec expects defined
// in commonMain/io/docxkt/pack/Codec.kt.
package io.docxkt.pack

import java.io.ByteArrayOutputStream
import java.util.zip.CRC32
import java.util.zip.Deflater as JdkDeflater
import java.util.zip.Inflater as JdkInflater

internal actual class Deflater actual constructor() {
    actual fun deflate(input: ByteArray): ByteArray {
        // nowrap = true → raw DEFLATE (RFC 1951), no zlib wrapper.
        // Default compression level ≈ 6, default strategy. These match
        // ZipOutputStream's defaults so byte-for-byte fixture diffs
        // stay green after the codec swap.
        val def = JdkDeflater(JdkDeflater.DEFAULT_COMPRESSION, true)
        try {
            def.setInput(input)
            def.finish()
            val sink = ByteArrayOutputStream(input.size + 64)
            val buf = ByteArray(4096)
            while (!def.finished()) {
                val n = def.deflate(buf)
                if (n > 0) sink.write(buf, 0, n)
            }
            return sink.toByteArray()
        } finally {
            def.end()
        }
    }
}

internal actual class Inflater actual constructor() {
    actual fun inflate(input: ByteArray): ByteArray {
        val inf = JdkInflater(true)
        try {
            inf.setInput(input)
            val sink = ByteArrayOutputStream(input.size * 2)
            val buf = ByteArray(4096)
            while (true) {
                val n = inf.inflate(buf)
                if (n > 0) sink.write(buf, 0, n)
                if (inf.finished()) break
                if (inf.needsDictionary()) error("Inflater: dictionary-needing stream")
                if (n == 0 && inf.needsInput()) error("Inflater: truncated stream")
                // n == 0 with !finished() and !needsInput() means the
                // output buffer was full; loop and consume more.
            }
            return sink.toByteArray()
        } finally {
            inf.end()
        }
    }
}

internal actual fun crc32(bytes: ByteArray): Long {
    val c = CRC32()
    c.update(bytes)
    return c.value
}

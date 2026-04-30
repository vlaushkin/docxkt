package io.docxkt.pack

import org.junit.jupiter.api.Test
import java.util.zip.CRC32 as JdkCrc32
import java.util.zip.Deflater as JdkDeflater
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

/**
 * Pins the codec contract on JVM: `Deflater` / `Inflater` / `crc32`
 * must match `java.util.zip` byte-for-byte (default level, nowrap).
 * The hand-rolled ZIP framer relies on this contract; if it ever
 * drifts, every fixture diff goes red — this test fails first with a
 * clear root cause.
 */
internal class CodecContractTest {

    @Test
    fun `Deflater matches JDK default-level nowrap output`() {
        val samples = listOf(
            ByteArray(0),
            "Hello, world!".toByteArray(),
            buildString { repeat(2048) { append("the quick brown fox ") } }.toByteArray(),
            ByteArray(4096) { (it and 0xFF).toByte() },
        )
        for (input in samples) {
            assertContentEquals(jdkDeflate(input), Deflater().deflate(input))
        }
    }

    @Test
    fun `Inflater round-trips Deflater output`() {
        val samples = listOf(
            ByteArray(0),
            "Round trip".toByteArray(),
            ByteArray(8192) { ((it * 31) and 0xFF).toByte() },
        )
        for (input in samples) {
            val deflated = Deflater().deflate(input)
            assertContentEquals(input, Inflater().inflate(deflated))
        }
    }

    @Test
    fun `crc32 matches java util zip CRC32`() {
        val samples = listOf(
            ByteArray(0),
            "abc".toByteArray(),
            "The quick brown fox jumps over the lazy dog".toByteArray(),
            ByteArray(1024) { (it and 0xFF).toByte() },
        )
        for (input in samples) {
            assertEquals(jdkCrc32(input), crc32(input))
        }
    }

    private fun jdkDeflate(input: ByteArray): ByteArray {
        val def = JdkDeflater(JdkDeflater.DEFAULT_COMPRESSION, true)
        try {
            def.setInput(input)
            def.finish()
            val sink = java.io.ByteArrayOutputStream(input.size + 64)
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

    private fun jdkCrc32(input: ByteArray): Long {
        val c = JdkCrc32()
        c.update(input)
        return c.value
    }
}

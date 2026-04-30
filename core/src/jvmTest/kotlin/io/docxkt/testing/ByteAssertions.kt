// Testing helper — no upstream analogue.
package io.docxkt.testing

import kotlin.test.fail

/**
 * Byte-equality assertion with a readable diff on mismatch.
 * Reports the first differing byte offset and dumps each side's
 * length — enough signal for a failing image test to point at the
 * bug without pastable hex dumps.
 */
internal fun assertBytesEqual(actual: ByteArray, expected: ByteArray, context: String) {
    if (actual.contentEquals(expected)) return
    val firstDiff = (0 until minOf(actual.size, expected.size))
        .firstOrNull { actual[it] != expected[it] }
        ?: minOf(actual.size, expected.size)
    fail(
        buildString {
            append("Binary mismatch in ").append(context).append('\n')
            append("  expected.size = ").append(expected.size).append('\n')
            append("  actual.size   = ").append(actual.size).append('\n')
            append("  first differing byte at offset ").append(firstDiff).append('\n')
            if (firstDiff < expected.size) {
                append("  expected[").append(firstDiff).append("] = 0x")
                    .append("%02X".format(expected[firstDiff])).append('\n')
            }
            if (firstDiff < actual.size) {
                append("  actual[").append(firstDiff).append("]   = 0x")
                    .append("%02X".format(actual[firstDiff])).append('\n')
            }
        }
    )
}

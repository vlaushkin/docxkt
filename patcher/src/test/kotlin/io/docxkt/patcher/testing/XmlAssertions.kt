// Testing helper — mirror of :core's XmlAssertions; same project
// convention.
package io.docxkt.patcher.testing

import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors
import kotlin.test.fail

internal fun assertXmlEquals(actualXml: String, expectedXml: String, context: String) {
    val diff = DiffBuilder.compare(Input.fromString(expectedXml))
        .withTest(Input.fromString(actualXml))
        .ignoreWhitespace()
        .withNodeMatcher(DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
        .checkForIdentical()
        .build()

    if (!diff.hasDifferences()) return

    val differences = diff.differences.joinToString("\n") { "  - $it" }
    fail(
        buildString {
            append("XML mismatch in ").append(context).append('\n')
            append(differences).append('\n')
            append("--- full XMLs ---\n")
            append("[EXPECTED]\n").append(expectedXml).append("\n\n")
            append("[ACTUAL]\n").append(actualXml).append('\n')
        }
    )
}

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
        }
    )
}

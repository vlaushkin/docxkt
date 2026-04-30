// Testing helper — no upstream analogue.
package io.docxkt.testing

import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors
import kotlin.test.fail

/**
 * XMLUnit-based XML equality with the project-standard configuration:
 * ignore whitespace (we don't pretty-print the same way upstream does),
 * match elements by tag + all attributes, and enforce identical semantics
 * (attribute-order strictness).
 */
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

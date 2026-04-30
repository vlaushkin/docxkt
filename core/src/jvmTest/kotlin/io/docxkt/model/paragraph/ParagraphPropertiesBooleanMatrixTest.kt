// No upstream analogue — boolean-matrix coverage of ParagraphProperties.
package io.docxkt.model.paragraph

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ParagraphPropertiesBooleanMatrixTest {

    @TestFactory
    fun `OnOff matrix`(): List<DynamicTest> {
        data class Case(val name: String, val element: String, val factory: (Boolean?) -> ParagraphProperties)
        val cases = listOf(
            Case("keepNext", "w:keepNext") { ParagraphProperties(keepNext = it) },
            Case("keepLines", "w:keepLines") { ParagraphProperties(keepLines = it) },
            Case("widowControl", "w:widowControl") { ParagraphProperties(widowControl = it) },
            Case("bidirectional", "w:bidi") { ParagraphProperties(bidirectional = it) },
            Case("contextualSpacing", "w:contextualSpacing") { ParagraphProperties(contextualSpacing = it) },
            Case("suppressLineNumbers", "w:suppressLineNumbers") { ParagraphProperties(suppressLineNumbers = it) },
        )
        return cases.flatMap { case ->
            listOf(
                dynamicTest("${case.name} null does not emit ${case.element}") {
                    assertFalse("<${case.element}" in render(case.factory(null)))
                },
                dynamicTest("${case.name} true emits self-closing ${case.element}") {
                    assertTrue("<${case.element}/>" in render(case.factory(true)))
                },
                dynamicTest("${case.name} false emits ${case.element} val false") {
                    assertTrue("""<${case.element} w:val="false"/>""" in render(case.factory(false)))
                },
            )
        }
    }

    // pageBreakBefore is a special truthy-only OnOff per upstream.
    @Test fun `pageBreakBefore null emits nothing`() {
        assertFalse("<w:pageBreakBefore" in render(ParagraphProperties(pageBreakBefore = null)))
    }

    @Test fun `pageBreakBefore false emits nothing - upstream truthy-only`() {
        // Upstream emits <w:pageBreakBefore/> only when truthy.
        assertFalse("<w:pageBreakBefore" in render(ParagraphProperties(pageBreakBefore = false)))
    }

    @Test fun `pageBreakBefore true emits self-closing element`() {
        assertTrue("<w:pageBreakBefore/>" in render(ParagraphProperties(pageBreakBefore = true)))
    }

    private fun render(p: ParagraphProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()
}

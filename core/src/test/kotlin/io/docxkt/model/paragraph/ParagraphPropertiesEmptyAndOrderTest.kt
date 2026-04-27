// No upstream analogue — empty-suppression and canonical-order
// tests for ParagraphProperties.
package io.docxkt.model.paragraph

import io.docxkt.model.border.BorderSide
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ParagraphPropertiesEmptyAndOrderTest {

    private fun render(p: ParagraphProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    // --- empty suppression ----------------------------------------------

    @Test fun `all-null ParagraphProperties emits empty string`() {
        assertEquals("", render(ParagraphProperties()))
    }

    @Test fun `empty Spacing alone is suppressed`() {
        assertEquals("", render(ParagraphProperties(spacing = Spacing())))
    }

    @Test fun `empty Indentation alone is suppressed`() {
        assertEquals("", render(ParagraphProperties(indentation = Indentation())))
    }

    @Test fun `pageBreakBefore false alone is suppressed - truthy-only`() {
        assertEquals("", render(ParagraphProperties(pageBreakBefore = false)))
    }

    @Test fun `single non-null field triggers wrapper emission`() {
        val xml = render(ParagraphProperties(keepNext = true))
        assertTrue(xml.startsWith("<w:pPr>"), "expected wrapper start; got $xml")
        assertTrue(xml.endsWith("</w:pPr>"), "expected wrapper end; got $xml")
    }

    // --- canonical order ------------------------------------------------

    @Test fun `pStyle precedes keepNext`() {
        val xml = render(ParagraphProperties(styleReference = "Heading1", keepNext = true))
        assertTrue(xml.indexOf("<w:pStyle ") < xml.indexOf("<w:keepNext"))
    }

    @Test fun `keepNext precedes keepLines`() {
        val xml = render(ParagraphProperties(keepNext = true, keepLines = true))
        assertTrue(xml.indexOf("<w:keepNext") < xml.indexOf("<w:keepLines"))
    }

    @Test fun `keepLines precedes pageBreakBefore`() {
        val xml = render(ParagraphProperties(keepLines = true, pageBreakBefore = true))
        assertTrue(xml.indexOf("<w:keepLines") < xml.indexOf("<w:pageBreakBefore"))
    }

    @Test fun `pBdr precedes shd`() {
        val xml = render(ParagraphProperties(
            borders = ParagraphBorders(top = BorderSide()),
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"),
        ))
        assertTrue(xml.indexOf("<w:pBdr") < xml.indexOf("<w:shd"))
    }

    @Test fun `bidi precedes spacing`() {
        val xml = render(ParagraphProperties(bidirectional = true, spacing = Spacing(before = 100)))
        assertTrue(xml.indexOf("<w:bidi") < xml.indexOf("<w:spacing"))
    }

    @Test fun `spacing precedes ind`() {
        val xml = render(ParagraphProperties(spacing = Spacing(before = 100), indentation = Indentation(left = 720)))
        assertTrue(xml.indexOf("<w:spacing") < xml.indexOf("<w:ind"))
    }

    @Test fun `ind precedes contextualSpacing`() {
        val xml = render(ParagraphProperties(indentation = Indentation(left = 720), contextualSpacing = true))
        assertTrue(xml.indexOf("<w:ind") < xml.indexOf("<w:contextualSpacing"))
    }

    @Test fun `contextualSpacing precedes jc`() {
        val xml = render(ParagraphProperties(contextualSpacing = true, alignment = AlignmentType.CENTER))
        assertTrue(xml.indexOf("<w:contextualSpacing") < xml.indexOf("<w:jc "))
    }

    @Test fun `jc precedes outlineLvl`() {
        val xml = render(ParagraphProperties(alignment = AlignmentType.CENTER, outlineLevel = 1))
        assertTrue(xml.indexOf("<w:jc ") < xml.indexOf("<w:outlineLvl "))
    }

    @Test fun `outlineLvl precedes suppressLineNumbers`() {
        val xml = render(ParagraphProperties(outlineLevel = 1, suppressLineNumbers = true))
        assertTrue(xml.indexOf("<w:outlineLvl ") < xml.indexOf("<w:suppressLineNumbers"))
    }

    @Test fun `kitchen-sink emits in upstream-canonical order`() {
        val xml = render(ParagraphProperties(
            styleReference = "Heading1",
            keepNext = true,
            keepLines = true,
            pageBreakBefore = true,
            widowControl = true,
            borders = ParagraphBorders(top = BorderSide()),
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"),
            bidirectional = true,
            spacing = Spacing(before = 100),
            indentation = Indentation(left = 720),
            contextualSpacing = true,
            alignment = AlignmentType.CENTER,
            outlineLevel = 1,
            suppressLineNumbers = true,
        ))
        val expected = listOf(
            "<w:pStyle ", "<w:keepNext", "<w:keepLines",
            "<w:pageBreakBefore", "<w:widowControl",
            "<w:pBdr", "<w:shd", "<w:bidi",
            "<w:spacing ", "<w:ind ", "<w:contextualSpacing",
            "<w:jc ", "<w:outlineLvl ", "<w:suppressLineNumbers",
        )
        var prev = -1
        for (probe in expected) {
            val idx = xml.indexOf(probe)
            assertTrue(idx >= 0, "missing $probe in $xml")
            assertTrue(idx > prev, "order broken at $probe in $xml")
            prev = idx
        }
    }
}

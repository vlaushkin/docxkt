// No upstream analogue — empty-suppression and canonical-order
// tests for RunProperties.
package io.docxkt.model.paragraph.run

import io.docxkt.model.border.BorderSide
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RunPropertiesEmptyAndOrderTest {

    private fun render(rPr: RunProperties): String =
        StringBuilder().apply { rPr.appendXml(this) }.toString()

    // --- empty suppression ----------------------------------------------

    @Test fun `all-null RunProperties emits empty string`() {
        // IgnoreIfEmptyXmlComponent skips the wrapper entirely when
        // every field is null.
        assertEquals("", render(RunProperties()))
    }

    @Test fun `vanish false alone is empty - truthy-only quirk`() {
        // vanish=false should NOT make rPr non-empty.
        assertEquals("", render(RunProperties(vanish = false)))
    }

    @Test fun `language with all-null is treated as empty`() {
        // RunProperties contains a Language whose isEmpty()==true →
        // doesn't trip the wrapper into emitting.
        assertEquals("", render(RunProperties(language = Language())))
    }

    @Test fun `single non-null field triggers wrapper emission`() {
        val xml = render(RunProperties(bold = true))
        assertTrue(xml.startsWith("<w:rPr>"), "expected <w:rPr> wrapper; got: $xml")
        assertTrue(xml.endsWith("</w:rPr>"), "expected </w:rPr> wrapper; got: $xml")
    }

    // --- canonical order ------------------------------------------------

    @Test fun `every field set produces children in upstream-canonical order`() {
        // One representative value per field; the assertion only
        // checks ORDER, not exact content.
        val rPr = RunProperties(
            styleReference = "Emphasis",
            font = Font.single("Arial"),
            bold = true,
            italics = true,
            smallCaps = true,
            strike = true,
            doubleStrike = true,
            emboss = true,
            imprint = true,
            noProof = true,
            snapToGrid = true,
            vanish = true,
            color = "FF0000",
            characterSpacing = 20,
            scale = 100,
            kern = 12,
            position = "6pt",
            size = 24,
            highlight = HighlightColor.YELLOW,
            underline = Underline(UnderlineType.SINGLE),
            textEffect = TextEffect.BLINK_BACKGROUND,
            border = BorderSide(),
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"),
            superScript = true,
            rightToLeft = true,
            emphasisMark = EmphasisMark.DOT,
            language = Language(value = "en-US"),
        )
        val xml = render(rPr)
        // Canonical order per upstream's properties.ts:
        // rStyle → rFonts → b/bCs → i/iCs → smallCaps/caps →
        // strike → dstrike → emboss → imprint → noProof →
        // snapToGrid → vanish → color → spacing → w → kern →
        // position → sz/szCs → highlight/highlightCs → u →
        // effect → bdr → shd → vertAlign → rtl → em → lang.
        val expectedOrder = listOf(
            "<w:rStyle ", "<w:rFonts ", "<w:b/>", "<w:bCs/>",
            "<w:i/>", "<w:iCs/>",
            "<w:smallCaps/>", "<w:strike/>", "<w:dstrike/>",
            "<w:emboss/>", "<w:imprint/>", "<w:noProof/>",
            "<w:snapToGrid/>", "<w:vanish/>",
            "<w:color ", "<w:spacing ", "<w:w ", "<w:kern ",
            "<w:position ", "<w:sz ", "<w:szCs ",
            "<w:highlight ", "<w:highlightCs ", "<w:u ",
            "<w:effect ", "<w:bdr ", "<w:shd ",
            "<w:vertAlign ", "<w:rtl/>", "<w:em ", "<w:lang ",
        )
        var prevIdx = -1
        for (probe in expectedOrder) {
            val idx = xml.indexOf(probe)
            assertTrue(idx >= 0, "missing $probe in $xml")
            assertTrue(idx > prevIdx, "order broken at $probe in $xml")
            prevIdx = idx
        }
    }

    @Test fun `bold precedes italics`() {
        val xml = render(RunProperties(bold = true, italics = true))
        assertTrue(xml.indexOf("<w:b/>") < xml.indexOf("<w:i/>"))
    }

    @Test fun `bCs precedes italics`() {
        // The bCs mirror sits before italics.
        val xml = render(RunProperties(bold = true, italics = true))
        assertTrue(xml.indexOf("<w:bCs/>") < xml.indexOf("<w:i/>"))
    }

    @Test fun `font precedes bold`() {
        val xml = render(RunProperties(font = Font.single("Arial"), bold = true))
        assertTrue(xml.indexOf("<w:rFonts ") < xml.indexOf("<w:b/>"))
    }

    @Test fun `styleReference precedes font`() {
        val xml = render(RunProperties(styleReference = "Foo", font = Font.single("Arial")))
        assertTrue(xml.indexOf("<w:rStyle ") < xml.indexOf("<w:rFonts "))
    }

    @Test fun `size precedes underline`() {
        val xml = render(RunProperties(size = 24, underline = Underline(UnderlineType.SINGLE)))
        assertTrue(xml.indexOf("<w:sz ") < xml.indexOf("<w:u "))
    }

    @Test fun `highlight precedes underline`() {
        val xml = render(RunProperties(highlight = HighlightColor.YELLOW, underline = Underline(UnderlineType.SINGLE)))
        assertTrue(xml.indexOf("<w:highlight ") < xml.indexOf("<w:u "))
    }

    @Test fun `vertAlign precedes rtl`() {
        val xml = render(RunProperties(superScript = true, rightToLeft = true))
        assertTrue(xml.indexOf("<w:vertAlign ") < xml.indexOf("<w:rtl/>"))
    }
}

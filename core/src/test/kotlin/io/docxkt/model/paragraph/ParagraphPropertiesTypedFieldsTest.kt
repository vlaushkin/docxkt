// No upstream analogue — typed-field coverage of ParagraphProperties.
package io.docxkt.model.paragraph

import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ParagraphPropertiesTypedFieldsTest {

    private fun render(p: ParagraphProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    // --- styleReference (pStyle) ---------------------------------------

    @Test fun `styleReference null emits nothing`() {
        assertFalse("<w:pStyle" in render(ParagraphProperties()))
    }

    @Test fun `styleReference emits w pStyle val`() {
        val xml = render(ParagraphProperties(styleReference = "Heading1"))
        assertTrue("""<w:pStyle w:val="Heading1"/>""" in xml)
    }

    // --- alignment ------------------------------------------------------

    @Test fun `alignment LEFT emits w jc val left`() {
        assertTrue("""<w:jc w:val="left"/>""" in render(ParagraphProperties(alignment = AlignmentType.LEFT)))
    }

    @Test fun `alignment CENTER emits w jc val center`() {
        assertTrue("""<w:jc w:val="center"/>""" in render(ParagraphProperties(alignment = AlignmentType.CENTER)))
    }

    @Test fun `alignment RIGHT emits w jc val right`() {
        assertTrue("""<w:jc w:val="right"/>""" in render(ParagraphProperties(alignment = AlignmentType.RIGHT)))
    }

    @Test fun `alignment START emits w jc val start`() {
        assertTrue("""<w:jc w:val="start"/>""" in render(ParagraphProperties(alignment = AlignmentType.START)))
    }

    @Test fun `alignment END emits w jc val end`() {
        assertTrue("""<w:jc w:val="end"/>""" in render(ParagraphProperties(alignment = AlignmentType.END)))
    }

    @Test fun `alignment BOTH emits w jc val both`() {
        assertTrue("""<w:jc w:val="both"/>""" in render(ParagraphProperties(alignment = AlignmentType.BOTH)))
    }

    @Test fun `alignment JUSTIFIED is alias for both - same wire as BOTH`() {
        // Upstream exposes JUSTIFIED as an alias for BOTH; both emit "both".
        val both = render(ParagraphProperties(alignment = AlignmentType.BOTH))
        val justified = render(ParagraphProperties(alignment = AlignmentType.JUSTIFIED))
        assertTrue(both == justified, "BOTH and JUSTIFIED should produce identical XML")
    }

    @Test fun `alignment DISTRIBUTE emits val distribute`() {
        assertTrue("""<w:jc w:val="distribute"/>""" in render(ParagraphProperties(alignment = AlignmentType.DISTRIBUTE)))
    }

    @Test fun `alignment THAI_DISTRIBUTE emits val thaiDistribute`() {
        assertTrue("""<w:jc w:val="thaiDistribute"/>""" in render(ParagraphProperties(alignment = AlignmentType.THAI_DISTRIBUTE)))
    }

    @Test fun `alignment MEDIUM_KASHIDA emits val mediumKashida`() {
        assertTrue("""<w:jc w:val="mediumKashida"/>""" in render(ParagraphProperties(alignment = AlignmentType.MEDIUM_KASHIDA)))
    }

    @Test fun `alignment HIGH_KASHIDA emits val highKashida`() {
        assertTrue("""<w:jc w:val="highKashida"/>""" in render(ParagraphProperties(alignment = AlignmentType.HIGH_KASHIDA)))
    }

    @Test fun `alignment LOW_KASHIDA emits val lowKashida`() {
        assertTrue("""<w:jc w:val="lowKashida"/>""" in render(ParagraphProperties(alignment = AlignmentType.LOW_KASHIDA)))
    }

    @Test fun `alignment NUM_TAB emits val numTab`() {
        assertTrue("""<w:jc w:val="numTab"/>""" in render(ParagraphProperties(alignment = AlignmentType.NUM_TAB)))
    }

    // --- outlineLevel ---------------------------------------------------

    @Test fun `outlineLevel emits w outlineLvl val`() {
        assertTrue("""<w:outlineLvl w:val="3"/>""" in render(ParagraphProperties(outlineLevel = 3)))
    }

    @Test fun `outlineLevel zero passes through`() {
        assertTrue("""<w:outlineLvl w:val="0"/>""" in render(ParagraphProperties(outlineLevel = 0)))
    }

    @Test fun `outlineLevel out-of-spec passes through verbatim`() {
        // OOXML's range is 0-9; we don't validate.
        assertTrue("""<w:outlineLvl w:val="20"/>""" in render(ParagraphProperties(outlineLevel = 20)))
    }

    // --- spacing --------------------------------------------------------

    @Test fun `spacing all-null is suppressed`() {
        // ParagraphProperties wraps spacing — empty Spacing doesn't emit.
        val xml = render(ParagraphProperties(spacing = Spacing()))
        assertFalse("<w:spacing" in xml)
    }

    @Test fun `spacing before only emits w spacing with before attr`() {
        val xml = render(ParagraphProperties(spacing = Spacing(before = 240)))
        assertTrue("""<w:spacing """ in xml)
        assertTrue("""w:before="240"""" in xml)
    }

    @Test fun `spacing after only emits w spacing with after attr`() {
        val xml = render(ParagraphProperties(spacing = Spacing(after = 480)))
        assertTrue("""w:after="480"""" in xml)
    }

    @Test fun `spacing line with lineRule emits both attrs`() {
        val xml = render(ParagraphProperties(spacing = Spacing(line = 360, lineRule = LineRule.AUTO)))
        assertTrue("""w:line="360"""" in xml)
        assertTrue("""w:lineRule="auto"""" in xml)
    }

    @Test fun `spacing autoSpacing booleans emit as 1 or 0`() {
        val xml = render(ParagraphProperties(spacing = Spacing(beforeAutoSpacing = true, afterAutoSpacing = false)))
        assertTrue("""w:beforeAutospacing="1"""" in xml)
        assertTrue("""w:afterAutospacing="0"""" in xml)
    }

    @Test fun `spacing attribute order is after-before-line per upstream BuilderElement`() {
        val xml = render(ParagraphProperties(spacing = Spacing(after = 200, before = 100, line = 300)))
        val a = xml.indexOf("w:after=")
        val b = xml.indexOf("w:before=")
        val l = xml.indexOf("w:line=")
        assertTrue(a < b && b < l, "expected after < before < line; got $xml")
    }

    // --- indentation ----------------------------------------------------

    @Test fun `indentation all-null is suppressed`() {
        assertFalse("<w:ind" in render(ParagraphProperties(indentation = Indentation())))
    }

    @Test fun `indentation start emits w ind start attr`() {
        assertTrue("""w:start="720"""" in render(ParagraphProperties(indentation = Indentation(start = 720))))
    }

    @Test fun `indentation hanging emits w ind hanging attr`() {
        assertTrue("""w:hanging="360"""" in render(ParagraphProperties(indentation = Indentation(hanging = 360))))
    }

    @Test fun `indentation firstLine emits w ind firstLine attr`() {
        assertTrue("""w:firstLine="360"""" in render(ParagraphProperties(indentation = Indentation(firstLine = 360))))
    }

    @Test fun `indentation negative passes through`() {
        // Pass-through; no validation that twips ≥ 0.
        assertTrue("""w:left="-100"""" in render(ParagraphProperties(indentation = Indentation(left = -100))))
    }

    @Test fun `indentation attribute order per upstream BuilderElement`() {
        val xml = render(ParagraphProperties(indentation = Indentation(start = 100, end = 200, left = 300, right = 400, hanging = 50, firstLine = 60)))
        val s = xml.indexOf("w:start=")
        val e = xml.indexOf("w:end=")
        val l = xml.indexOf("w:left=")
        val r = xml.indexOf("w:right=")
        val h = xml.indexOf("w:hanging=")
        val f = xml.indexOf("w:firstLine=")
        // start, end, left, right, hanging, firstLine
        assertTrue(s < e && e < l && l < r && r < h && h < f)
    }

    // --- shading --------------------------------------------------------

    @Test fun `shading clear emits w shd val clear`() {
        val xml = render(ParagraphProperties(shading = Shading(pattern = ShadingPattern.CLEAR, fill = "DDDDDD")))
        assertTrue("<w:shd " in xml)
        assertTrue("""w:val="clear"""" in xml)
    }
}

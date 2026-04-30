// No upstream analogue — Kotlin test of typed (non-OnOff) fields
// on RunProperties: enums, value types, hex normalization.
package io.docxkt.model.paragraph.run

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class RunPropertiesTypedFieldsTest {

    private fun render(rPr: RunProperties): String =
        StringBuilder().apply { rPr.appendXml(this) }.toString()

    // --- styleReference (rStyle) ---------------------------------------

    @Test fun `styleReference null emits nothing`() {
        assertFalse("<w:rStyle" in render(RunProperties()))
    }

    @Test fun `styleReference emits w rStyle val`() {
        val xml = render(RunProperties(styleReference = "Emphasis"))
        assertTrue("""<w:rStyle w:val="Emphasis"/>""" in xml)
    }

    // --- color + hex normalization -------------------------------------

    @Test fun `color null emits nothing`() {
        assertFalse("<w:color" in render(RunProperties()))
    }

    @Test fun `color hex string emits w color val`() {
        val xml = render(RunProperties(color = "FF0000"))
        assertTrue("""<w:color w:val="FF0000"/>""" in xml)
    }

    @Test fun `color leading hash strips to bare hex`() {
        val xml = render(RunProperties(color = "#FF0000"))
        assertTrue("""<w:color w:val="FF0000"/>""" in xml)
    }

    @Test fun `color literal auto passes through`() {
        val xml = render(RunProperties(color = "auto"))
        assertTrue("""<w:color w:val="auto"/>""" in xml)
    }

    @Test fun `color malformed hex passes through verbatim`() {
        // Pass-through policy: no validation.
        val xml = render(RunProperties(color = "ZZZZZZ"))
        assertTrue("""<w:color w:val="ZZZZZZ"/>""" in xml)
    }

    // --- size (half-points) ---------------------------------------------

    @Test fun `size emits w sz and w szCs mirror`() {
        val xml = render(RunProperties(size = 24))
        assertTrue("""<w:sz w:val="24"/>""" in xml)
        assertTrue("""<w:szCs w:val="24"/>""" in xml)
    }

    @Test fun `size negative passes through verbatim`() {
        val xml = render(RunProperties(size = -10))
        assertTrue("""<w:sz w:val="-10"/>""" in xml)
    }

    @Test fun `size zero passes through`() {
        val xml = render(RunProperties(size = 0))
        assertTrue("""<w:sz w:val="0"/>""" in xml)
    }

    // --- characterSpacing / scale / kern / position ---------------------

    @Test fun `characterSpacing emits w spacing val`() {
        val xml = render(RunProperties(characterSpacing = 20))
        assertTrue("""<w:spacing w:val="20"/>""" in xml)
    }

    @Test fun `characterSpacing negative passes through`() {
        val xml = render(RunProperties(characterSpacing = -15))
        assertTrue("""<w:spacing w:val="-15"/>""" in xml)
    }

    @Test fun `scale emits w w val percentage`() {
        val xml = render(RunProperties(scale = 150))
        assertTrue("""<w:w w:val="150"/>""" in xml)
    }

    @Test fun `kern emits w kern val half-points`() {
        val xml = render(RunProperties(kern = 12))
        assertTrue("""<w:kern w:val="12"/>""" in xml)
    }

    @Test fun `position emits w position val verbatim`() {
        // Upstream does no unit parsing; we pass through.
        val xml = render(RunProperties(position = "6pt"))
        assertTrue("""<w:position w:val="6pt"/>""" in xml)
    }

    @Test fun `position negative pt value passes through`() {
        val xml = render(RunProperties(position = "-3pt"))
        assertTrue("""<w:position w:val="-3pt"/>""" in xml)
    }

    // --- font (single + per-script) ------------------------------------

    @Test fun `single font emits w rFonts with all four attrs`() {
        val xml = render(RunProperties(font = Font.single("Arial")))
        assertTrue("""<w:rFonts """ in xml)
        assertTrue("""w:ascii="Arial"""" in xml)
        assertTrue("""w:hAnsi="Arial"""" in xml)
        assertTrue("""w:cs="Arial"""" in xml)
        assertTrue("""w:eastAsia="Arial"""" in xml)
    }

    @Test fun `font with hint emits w hint`() {
        val xml = render(RunProperties(font = Font.single("Arial", hint = "default")))
        assertTrue("""w:hint="default"""" in xml)
    }

    @Test fun `per-script font emits only the set attrs`() {
        val xml = render(RunProperties(font = Font.perScript(ascii = "Arial", cs = "DengXian")))
        assertTrue("""w:ascii="Arial"""" in xml)
        assertTrue("""w:cs="DengXian"""" in xml)
        assertFalse("""w:hAnsi=""" in xml)
        assertFalse("""w:eastAsia=""" in xml)
    }

    // --- underline ------------------------------------------------------

    @Test fun `underline emits w u with val`() {
        val xml = render(RunProperties(underline = Underline(UnderlineType.SINGLE)))
        assertTrue("""<w:u w:val="single"/>""" in xml)
    }

    @Test fun `underline with color emits both attrs`() {
        val xml = render(RunProperties(underline = Underline(UnderlineType.DOUBLE, color = "FF0000")))
        assertTrue("""<w:u w:val="double" w:color="FF0000"/>""" in xml)
    }

    @Test fun `underline color hash-prefix strips`() {
        val xml = render(RunProperties(underline = Underline(UnderlineType.SINGLE, color = "#00FF00")))
        assertTrue("""w:color="00FF00"""" in xml)
    }

    @Test fun `underline none type emits val none`() {
        val xml = render(RunProperties(underline = Underline(UnderlineType.NONE)))
        assertTrue("""<w:u w:val="none"/>""" in xml)
    }

    // --- highlight ------------------------------------------------------

    @Test fun `highlight emits w highlight and w highlightCs mirror`() {
        val xml = render(RunProperties(highlight = HighlightColor.YELLOW))
        assertTrue("""<w:highlight w:val="yellow"/>""" in xml)
        assertTrue("""<w:highlightCs w:val="yellow"/>""" in xml)
    }

    @Test fun `highlight none token`() {
        val xml = render(RunProperties(highlight = HighlightColor.NONE))
        assertTrue("""<w:highlight w:val="none"/>""" in xml)
    }

    // --- textEffect -----------------------------------------------------

    @Test fun `textEffect blink emits w effect val blinkBackground`() {
        val xml = render(RunProperties(textEffect = TextEffect.BLINK_BACKGROUND))
        assertTrue("""<w:effect w:val="blinkBackground"/>""" in xml)
    }

    @Test fun `textEffect lights emits val lights`() {
        val xml = render(RunProperties(textEffect = TextEffect.LIGHTS))
        assertTrue("""<w:effect w:val="lights"/>""" in xml)
    }

    // --- border (run-level w bdr) --------------------------------------

    @Test fun `border emits w bdr with val and color`() {
        val xml = render(RunProperties(border = BorderSide(style = BorderStyle.SINGLE, size = 4, color = "FF0000")))
        assertTrue("""<w:bdr w:val="single" w:color="FF0000" w:sz="4"/>""" in xml)
    }

    // --- shading --------------------------------------------------------

    @Test fun `shading clear pattern emits w shd`() {
        val xml = render(RunProperties(shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE")))
        assertTrue("<w:shd " in xml)
        assertTrue("""w:val="clear"""" in xml)
        assertTrue("""w:fill="EEEEEE"""" in xml)
    }

    // --- emphasisMark ---------------------------------------------------

    @Test fun `emphasisMark dot emits w em val dot`() {
        val xml = render(RunProperties(emphasisMark = EmphasisMark.DOT))
        assertTrue("""<w:em w:val="dot"/>""" in xml)
    }

    // --- language -------------------------------------------------------

    @Test fun `language with value emits w lang`() {
        val xml = render(RunProperties(language = Language(value = "en-US")))
        assertTrue("""<w:lang w:val="en-US"/>""" in xml)
    }

    @Test fun `language with eastAsia emits attribute`() {
        val xml = render(RunProperties(language = Language(eastAsia = "ja-JP")))
        assertTrue("""<w:lang w:eastAsia="ja-JP"/>""" in xml)
    }

    @Test fun `language with all three emits all attrs`() {
        val xml = render(RunProperties(language = Language(value = "en-US", eastAsia = "ja-JP", bidirectional = "ar-SA")))
        assertTrue("""w:val="en-US"""" in xml)
        assertTrue("""w:eastAsia="ja-JP"""" in xml)
        assertTrue("""w:bidi="ar-SA"""" in xml)
    }

    @Test fun `language fully empty is suppressed`() {
        // Language(null,null,null) is empty — should not produce <w:lang>.
        val xml = render(RunProperties(language = Language()))
        assertFalse("<w:lang" in xml)
    }
}

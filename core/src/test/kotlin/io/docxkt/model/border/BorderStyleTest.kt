// No upstream analogue — wire mapping coverage for BorderStyle and
// BorderSide.
package io.docxkt.model.border

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class BorderStyleTest {

    @Test fun `SINGLE wire is single`() = assertEquals("single", BorderStyle.SINGLE.wire)
    @Test fun `DOUBLE wire is double`() = assertEquals("double", BorderStyle.DOUBLE.wire)
    @Test fun `DASHED wire is dashed`() = assertEquals("dashed", BorderStyle.DASHED.wire)
    @Test fun `DOTTED wire is dotted`() = assertEquals("dotted", BorderStyle.DOTTED.wire)
    @Test fun `NONE wire is none`() = assertEquals("none", BorderStyle.NONE.wire)
    @Test fun `NIL wire is nil`() = assertEquals("nil", BorderStyle.NIL.wire)
    @Test fun `THICK wire is thick`() = assertEquals("thick", BorderStyle.THICK.wire)
    @Test fun `INSET wire is inset`() = assertEquals("inset", BorderStyle.INSET.wire)
    @Test fun `OUTSET wire is outset`() = assertEquals("outset", BorderStyle.OUTSET.wire)
    @Test fun `DASH_DOT_STROKED wire camelCase`() =
        assertEquals("dashDotStroked", BorderStyle.DASH_DOT_STROKED.wire)
    @Test fun `DASH_SMALL_GAP wire camelCase`() =
        assertEquals("dashSmallGap", BorderStyle.DASH_SMALL_GAP.wire)
    @Test fun `DOT_DASH wire camelCase`() = assertEquals("dotDash", BorderStyle.DOT_DASH.wire)
    @Test fun `DOT_DOT_DASH wire camelCase`() = assertEquals("dotDotDash", BorderStyle.DOT_DOT_DASH.wire)
    @Test fun `DOUBLE_WAVE wire camelCase`() = assertEquals("doubleWave", BorderStyle.DOUBLE_WAVE.wire)
    @Test fun `THICK_THIN_LARGE_GAP wire camelCase`() =
        assertEquals("thickThinLargeGap", BorderStyle.THICK_THIN_LARGE_GAP.wire)
    @Test fun `THIN_THICK_LARGE_GAP wire camelCase`() =
        assertEquals("thinThickLargeGap", BorderStyle.THIN_THICK_LARGE_GAP.wire)
}

internal class BorderSideEdgeCasesTest {

    private fun render(name: String, side: BorderSide): String =
        StringBuilder().apply { writeBorderSide(this, name, side) }.toString()

    @Test fun `attribute order is val-color-sz-space when all set`() {
        val xml = render("w:top", BorderSide(
            style = BorderStyle.DOUBLE, size = 8, color = "FF0000", space = 4,
        ))
        val v = xml.indexOf("w:val=")
        val c = xml.indexOf("w:color=")
        val s = xml.indexOf("w:sz=")
        val sp = xml.indexOf("w:space=")
        assertTrue(v in 0 until c)
        assertTrue(c in 0 until s)
        assertTrue(s in 0 until sp)
    }

    @Test fun `null size omits sz attr`() {
        val xml = render("w:top", BorderSide(size = null))
        assertFalse("w:sz=" in xml)
    }

    @Test fun `null color omits color attr`() {
        val xml = render("w:top", BorderSide(color = null))
        assertFalse("w:color=" in xml)
    }

    @Test fun `null space omits space attr`() {
        val xml = render("w:top", BorderSide(space = null))
        assertFalse("w:space=" in xml)
    }

    @Test fun `custom element name propagates`() {
        val xml = render("w:between", BorderSide())
        assertTrue(xml.startsWith("<w:between "))
        assertTrue(xml.endsWith("/>"))
    }

    @Test fun `data class equality`() {
        assertEquals(
            BorderSide(BorderStyle.DASHED, 8, "FF0000", 4),
            BorderSide(BorderStyle.DASHED, 8, "FF0000", 4),
        )
    }

    @Test fun `negative size pass-through`() {
        val xml = render("w:top", BorderSide(size = -1))
        assertTrue("""w:sz="-1"""" in xml)
    }

    @Test fun `auto color literal pass-through`() {
        val xml = render("w:top", BorderSide(color = "auto"))
        assertTrue("""w:color="auto"""" in xml)
    }

    @Test fun `large hex color value pass-through`() {
        // 8-digit hex; pass-through, no validation.
        val xml = render("w:top", BorderSide(color = "FFAABBCC"))
        assertTrue("""w:color="FFAABBCC"""" in xml)
    }
}

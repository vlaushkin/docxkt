package io.docxkt.model.border

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BorderSideTest {

    @Test
    fun `default BorderSide matches upstream's DEFAULT_BORDER shape`() {
        val side = BorderSide()
        assertEquals(BorderStyle.SINGLE, side.style)
        assertEquals(4, side.size)
        assertEquals("auto", side.color)
        assertEquals(null, side.space)
    }

    @Test
    fun `UPSTREAM_DEFAULT constant is used as the fill for unset sides`() {
        assertEquals(BorderStyle.SINGLE, BorderSide.UPSTREAM_DEFAULT.style)
        assertEquals(4, BorderSide.UPSTREAM_DEFAULT.size)
        assertEquals("auto", BorderSide.UPSTREAM_DEFAULT.color)
        assertEquals(null, BorderSide.UPSTREAM_DEFAULT.space)
    }

    @Test
    fun `writeBorderSide default emits attribute order val color sz with no space`() {
        val out = StringBuilder()
        writeBorderSide(out, "w:top", BorderSide.UPSTREAM_DEFAULT)
        assertEquals("""<w:top w:val="single" w:color="auto" w:sz="4"/>""", out.toString())
    }

    @Test
    fun `writeBorderSide includes space when set`() {
        val out = StringBuilder()
        writeBorderSide(out, "w:top", BorderSide(
            style = BorderStyle.DOUBLE, size = 12, color = "FF0000", space = 1,
        ))
        assertEquals(
            """<w:top w:val="double" w:color="FF0000" w:sz="12" w:space="1"/>""",
            out.toString(),
        )
    }

    @Test
    fun `null size and color omit their attributes`() {
        val out = StringBuilder()
        writeBorderSide(out, "w:top", BorderSide(
            style = BorderStyle.THICK, size = null, color = null, space = null,
        ))
        assertEquals("""<w:top w:val="thick"/>""", out.toString())
    }
}

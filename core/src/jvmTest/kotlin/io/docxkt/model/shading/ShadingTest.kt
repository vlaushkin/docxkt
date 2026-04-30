// No upstream analogue — Shading + writeShading helper coverage.
package io.docxkt.model.shading

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ShadingTest {

    @Test fun `pattern CLEAR wire`() = assertEquals("clear", ShadingPattern.CLEAR.wire)
    @Test fun `pattern SOLID wire`() = assertEquals("solid", ShadingPattern.SOLID.wire)
    @Test fun `pattern NIL wire`() = assertEquals("nil", ShadingPattern.NIL_PATTERN.wire)
    @Test fun `enum mirrors upstream's full ShadingType set`() =
        assertEquals(38, ShadingPattern.values().size)

    @Test fun `data class equality`() {
        assertEquals(
            Shading(pattern = ShadingPattern.CLEAR, fill = "FF0000"),
            Shading(pattern = ShadingPattern.CLEAR, fill = "FF0000"),
        )
    }

    @Test fun `data class differs on pattern`() {
        assertEquals(false,
            Shading(pattern = ShadingPattern.CLEAR) ==
            Shading(pattern = ShadingPattern.SOLID),
        )
    }

    @Test fun `default color and fill are null`() {
        val s = Shading(pattern = ShadingPattern.CLEAR)
        assertEquals(null, s.color)
        assertEquals(null, s.fill)
    }
}

internal class WriteShadingTest {

    private fun render(s: Shading): String =
        StringBuilder().apply { writeShading(this, s) }.toString()

    @Test fun `pattern only emits w shd with val attr only`() {
        val xml = render(Shading(pattern = ShadingPattern.CLEAR))
        assertEquals("""<w:shd w:val="clear"/>""", xml)
    }

    @Test fun `fill emits w fill attr`() {
        val xml = render(Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"))
        assertTrue("""w:fill="EEEEEE"""" in xml)
    }

    @Test fun `color emits w color attr`() {
        val xml = render(Shading(pattern = ShadingPattern.CLEAR, color = "auto"))
        assertTrue("""w:color="auto"""" in xml)
    }

    @Test fun `attribute order is fill-color-val per upstream BuilderElement`() {
        // Required attribute "val" is LAST — non-intuitive.
        val xml = render(Shading(pattern = ShadingPattern.SOLID, color = "FF0000", fill = "00FF00"))
        val f = xml.indexOf("w:fill=")
        val c = xml.indexOf("w:color=")
        val v = xml.indexOf("w:val=")
        assertTrue(f in 0 until c, "expected fill before color; got $xml")
        assertTrue(c in 0 until v, "expected color before val; got $xml")
    }

    @Test fun `null fill skips attr`() {
        val xml = render(Shading(pattern = ShadingPattern.CLEAR, fill = null, color = "auto"))
        assertFalse("w:fill=" in xml)
    }

    @Test fun `null color skips attr`() {
        val xml = render(Shading(pattern = ShadingPattern.CLEAR, fill = "FF0000", color = null))
        assertFalse("w:color=" in xml)
    }

    @Test fun `solid pattern wire is solid`() {
        val xml = render(Shading(pattern = ShadingPattern.SOLID))
        assertTrue("""w:val="solid"""" in xml)
    }

    @Test fun `nil pattern wire is nil`() {
        val xml = render(Shading(pattern = ShadingPattern.NIL_PATTERN))
        assertTrue("""w:val="nil"""" in xml)
    }
}

// No upstream analogue — boolean/order matrix for ParagraphBorders.
package io.docxkt.model.paragraph

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ParagraphBordersTest {

    private fun render(b: ParagraphBorders): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `all-null is suppressed`() {
        assertEquals("", render(ParagraphBorders()))
    }

    @Test fun `top only emits w pBdr with w top child`() {
        val xml = render(ParagraphBorders(top = BorderSide(style = BorderStyle.SINGLE)))
        assertTrue("<w:pBdr>" in xml)
        assertTrue("<w:top " in xml)
        assertFalse("<w:bottom " in xml)
        assertFalse("<w:left " in xml)
        assertFalse("<w:right " in xml)
        assertFalse("<w:between " in xml)
    }

    @Test fun `bottom only emits w bottom child`() {
        val xml = render(ParagraphBorders(bottom = BorderSide()))
        assertTrue("<w:bottom " in xml)
    }

    @Test fun `left only emits w left child`() {
        val xml = render(ParagraphBorders(left = BorderSide()))
        assertTrue("<w:left " in xml)
    }

    @Test fun `right only emits w right child`() {
        val xml = render(ParagraphBorders(right = BorderSide()))
        assertTrue("<w:right " in xml)
    }

    @Test fun `between only emits w between child`() {
        val xml = render(ParagraphBorders(between = BorderSide()))
        assertTrue("<w:between " in xml)
    }

    @Test fun `border attrs val color sz space in canonical order`() {
        val xml = render(ParagraphBorders(top = BorderSide(
            style = BorderStyle.DOUBLE,
            color = "FF0000",
            size = 8,
            space = 4,
        )))
        // Attribute order val → color → sz → space.
        val v = xml.indexOf("w:val=")
        val c = xml.indexOf("w:color=")
        val s = xml.indexOf("w:sz=")
        val sp = xml.indexOf("w:space=")
        assertTrue(v < c && c < s && s < sp, "expected val < color < sz < space; got $xml")
    }

    @Test fun `all five sides emit in upstream order top-bottom-left-right-between`() {
        // Upstream's emission order is NOT XSD canonical (top-left-bottom-
        // right-between); it's top → bottom → left → right → between.
        val xml = render(ParagraphBorders(
            top = BorderSide(),
            bottom = BorderSide(),
            left = BorderSide(),
            right = BorderSide(),
            between = BorderSide(),
        ))
        val t = xml.indexOf("<w:top ")
        val b = xml.indexOf("<w:bottom ")
        val l = xml.indexOf("<w:left ")
        val r = xml.indexOf("<w:right ")
        val be = xml.indexOf("<w:between ")
        assertTrue(t < b && b < l && l < r && r < be, "expected top < bottom < left < right < between; got $xml")
    }

    @Test fun `border style enum maps to wire token`() {
        val xml = render(ParagraphBorders(top = BorderSide(style = BorderStyle.DASHED)))
        assertTrue("""w:val="dashed"""" in xml)
    }

    @Test fun `border with auto color passes through`() {
        val xml = render(ParagraphBorders(top = BorderSide(color = "auto")))
        assertTrue("""w:color="auto"""" in xml)
    }

    @Test fun `border null size omits sz attr`() {
        val xml = render(ParagraphBorders(top = BorderSide(size = null)))
        assertFalse("w:sz=" in xml)
    }

    @Test fun `border null color omits color attr`() {
        val xml = render(ParagraphBorders(top = BorderSide(color = null)))
        assertFalse("w:color=" in xml)
    }
}

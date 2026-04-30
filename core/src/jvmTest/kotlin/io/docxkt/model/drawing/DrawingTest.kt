// No upstream analogue — Drawing wire shape and ImageSlot semantics.
package io.docxkt.model.drawing

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertTrue

internal class ImageSlotTest {

    @Test fun `unresolved slot throws on rid access`() {
        val slot = ImageSlot()
        assertFails { slot.rid }
    }

    @Test fun `slot returns resolved rid after assignment`() {
        val slot = ImageSlot()
        slot.resolvedRid = "rId7"
        assertEquals("rId7", slot.rid)
    }

    @Test fun `slot rid throws includes meaningful message`() {
        val slot = ImageSlot()
        try {
            slot.rid
            error("expected throw")
        } catch (t: IllegalStateException) {
            assertTrue("ImageSlot" in (t.message ?: ""))
        }
    }
}

internal class DrawingTest {

    private fun render(d: Drawing): String =
        StringBuilder().apply { d.appendXml(this) }.toString()

    private fun resolvedSlot(rid: String): ImageSlot =
        ImageSlot().apply { resolvedRid = rid }

    @Test fun `Drawing emits w drawing wrapper`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1_000_000,
            heightEmus = 500_000,
        ))
        assertTrue(xml.startsWith("<w:drawing>"))
        assertTrue(xml.endsWith("</w:drawing>"))
    }

    @Test fun `Drawing emits wp inline with all four distance attrs as zero`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 100, heightEmus = 200,
        ))
        assertTrue("""<wp:inline distT="0" distB="0" distL="0" distR="0">""" in xml)
    }

    @Test fun `Drawing extent emits cx and cy from emus`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 952500, heightEmus = 1905000,
        ))
        assertTrue("""<wp:extent cx="952500" cy="1905000"/>""" in xml)
    }

    @Test fun `Drawing effectExtent emits all four sides as zero`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
        ))
        assertTrue("""<wp:effectExtent t="0" r="0" b="0" l="0"/>""" in xml)
    }

    @Test fun `Drawing references resolved rId in a blip embed`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId42"),
            widthEmus = 1, heightEmus = 1,
        ))
        assertTrue("""r:embed="rId42"""" in xml)
    }

    @Test fun `Drawing docPr id default is 1`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
        ))
        assertTrue("""id="1"""" in xml)
    }

    @Test fun `Drawing accepts custom docPr id`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
            docPrId = 7,
        ))
        assertTrue("""id="7"""" in xml)
    }

    @Test fun `Drawing emits a graphic with picture data uri`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
        ))
        assertTrue("""<a:graphic """ in xml)
        assertTrue("""<a:graphicData """ in xml)
        assertTrue("""drawingml/2006/picture""" in xml)
    }

    @Test fun `Drawing pic spPr extent matches widthEmus heightEmus`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 952500, heightEmus = 952500,
        ))
        // <a:ext cx="952500" cy="952500"/>
        assertTrue("""cx="952500"""" in xml)
        assertTrue("""cy="952500"""" in xml)
    }

    @Test fun `Drawing throws when slot unresolved`() {
        val unresolved = ImageSlot()
        val drawing = Drawing(embedSlot = unresolved, widthEmus = 1, heightEmus = 1)
        assertFails { render(drawing) }
    }

    @Test fun `Drawing description leaves docPr descr empty when null`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
            description = null,
        ))
        assertTrue("""descr=""""" in xml)
    }

    @Test fun `Drawing description when set propagates to docPr`() {
        val xml = render(Drawing(
            embedSlot = resolvedSlot("rId1"),
            widthEmus = 1, heightEmus = 1,
            description = "alt-text",
        ))
        assertTrue("""descr="alt-text"""" in xml)
    }
}

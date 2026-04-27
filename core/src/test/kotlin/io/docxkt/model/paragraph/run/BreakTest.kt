package io.docxkt.model.paragraph.run

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class BreakTest {

    @Test
    fun `LINE emits attribute-free self-closing tag`() {
        val out = StringBuilder()
        Break(BreakType.LINE).appendXml(out)
        assertEquals("<w:br/>", out.toString())
    }

    @Test
    fun `PAGE emits w_type page`() {
        val out = StringBuilder()
        Break(BreakType.PAGE).appendXml(out)
        assertEquals("""<w:br w:type="page"/>""", out.toString())
    }

    @Test
    fun `TEXT_WRAPPING emits w_type textWrapping`() {
        val out = StringBuilder()
        Break(BreakType.TEXT_WRAPPING).appendXml(out)
        assertEquals("""<w:br w:type="textWrapping"/>""", out.toString())
    }

    @Test
    fun `default type is LINE`() {
        val out = StringBuilder()
        Break().appendXml(out)
        assertEquals("<w:br/>", out.toString())
    }
}

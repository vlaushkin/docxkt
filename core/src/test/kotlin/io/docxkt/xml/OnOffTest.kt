package io.docxkt.xml

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class OnOffTest {

    @Test
    fun `null emits nothing`() {
        val out = StringBuilder()
        out.onOff("w:b", null)
        assertEquals("", out.toString())
    }

    @Test
    fun `true emits attribute-free self-closing tag`() {
        val out = StringBuilder()
        out.onOff("w:b", true)
        assertEquals("<w:b/>", out.toString())
    }

    @Test
    fun `false emits w val=false never the redundant val=true form`() {
        val out = StringBuilder()
        out.onOff("w:b", false)
        assertEquals("""<w:b w:val="false"/>""", out.toString())
    }

    @Test
    fun `different element names pass through`() {
        val out = StringBuilder()
        out.onOff("w:smallCaps", true)
        out.onOff("w:strike", false)
        out.onOff("w:italics", null)
        assertEquals("""<w:smallCaps/><w:strike w:val="false"/>""", out.toString())
    }
}

// No upstream analogue — coverage of the Elements helper functions
// that all XmlComponents call into for opening/self-closing tags.
package io.docxkt.xml

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class ElementsTest {

    // --- selfClosingElement --------------------------------------------

    @Test fun `selfClosingElement no attrs emits bare self-closing tag`() {
        val out = StringBuilder()
        out.selfClosingElement("w:b")
        assertEquals("<w:b/>", out.toString())
    }

    @Test fun `selfClosingElement single attr emits with attr`() {
        val out = StringBuilder()
        out.selfClosingElement("w:val", "w:val" to "true")
        assertEquals("""<w:val w:val="true"/>""", out.toString())
    }

    @Test fun `selfClosingElement skips null attribute values`() {
        val out = StringBuilder()
        out.selfClosingElement("w:x", "w:a" to "1", "w:b" to null, "w:c" to "3")
        assertEquals("""<w:x w:a="1" w:c="3"/>""", out.toString())
    }

    @Test fun `selfClosingElement preserves attribute insertion order`() {
        val out = StringBuilder()
        out.selfClosingElement("w:x", "z" to "1", "a" to "2", "m" to "3")
        // No alphabetization — author order is the wire order.
        assertEquals("""<w:x z="1" a="2" m="3"/>""", out.toString())
    }

    @Test fun `selfClosingElement escapes attribute values`() {
        val out = StringBuilder()
        out.selfClosingElement("w:x", "v" to """a"b<c>""")
        assertEquals("""<w:x v="a&quot;b&lt;c&gt;"/>""", out.toString())
    }

    // --- openElement ----------------------------------------------------

    @Test fun `openElement emits start tag with attrs`() {
        val out = StringBuilder()
        out.openElement("w:p", "w:rsidR" to "00000000")
        assertEquals("""<w:p w:rsidR="00000000">""", out.toString())
    }

    @Test fun `openElement no attrs emits bare start tag`() {
        val out = StringBuilder()
        out.openElement("w:body")
        assertEquals("<w:body>", out.toString())
    }

    // --- closeElement ---------------------------------------------------

    @Test fun `closeElement emits closing tag`() {
        val out = StringBuilder()
        out.closeElement("w:body")
        assertEquals("</w:body>", out.toString())
    }

    // --- textElement ----------------------------------------------------

    @Test fun `textElement wraps escaped text content`() {
        val out = StringBuilder()
        out.textElement("w:t", "a < b")
        assertEquals("<w:t>a &lt; b</w:t>", out.toString())
    }

    @Test fun `textElement carries attributes`() {
        val out = StringBuilder()
        out.textElement("w:t", "x", "xml:space" to "preserve")
        assertEquals("""<w:t xml:space="preserve">x</w:t>""", out.toString())
    }

    @Test fun `textElement skips null attributes`() {
        val out = StringBuilder()
        out.textElement("w:t", "x", "a" to null, "b" to "1")
        assertEquals("""<w:t b="1">x</w:t>""", out.toString())
    }

    // --- appendXmlDeclaration -------------------------------------------

    @Test fun `appendXmlDeclaration default (not standalone) emits short prelude`() {
        val out = StringBuilder()
        out.appendXmlDeclaration()
        assertEquals("""<?xml version="1.0" encoding="UTF-8"?>""", out.toString())
    }

    @Test fun `appendXmlDeclaration standalone yes emits standalone attr`() {
        val out = StringBuilder()
        out.appendXmlDeclaration(standalone = true)
        assertEquals("""<?xml version="1.0" encoding="UTF-8" standalone="yes"?>""", out.toString())
    }
}

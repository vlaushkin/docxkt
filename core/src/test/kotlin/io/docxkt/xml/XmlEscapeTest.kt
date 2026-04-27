package io.docxkt.xml

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class XmlEscapeTest {

    @Test
    fun `escapeText leaves ordinary text unchanged`() {
        assertEquals("Hello, world!", XmlEscape.escapeText("Hello, world!"))
    }

    @Test
    fun `escapeText escapes the three text-significant characters`() {
        assertEquals("a &amp; b", XmlEscape.escapeText("a & b"))
        assertEquals("1 &lt; 2", XmlEscape.escapeText("1 < 2"))
        assertEquals("3 &gt; 2", XmlEscape.escapeText("3 > 2"))
    }

    @Test
    fun `escapeText does not escape quotes in text`() {
        // Quotes are legal in text content; only attribute values need them.
        assertEquals("\"quoted\" and 'apostrophed'", XmlEscape.escapeText("\"quoted\" and 'apostrophed'"))
    }

    @Test
    fun `escapeAttributeValue escapes quotes and whitespace`() {
        assertEquals("a &quot;b&quot; c", XmlEscape.escapeAttributeValue("a \"b\" c"))
        assertEquals("a &apos;b&apos; c", XmlEscape.escapeAttributeValue("a 'b' c"))
        assertEquals("tab&#x9;here", XmlEscape.escapeAttributeValue("tab\there"))
        assertEquals("cr&#xD;lf&#xA;end", XmlEscape.escapeAttributeValue("cr\rlf\nend"))
    }

    @Test
    fun `escapeAttributeValue escapes ampersand and angle brackets`() {
        assertEquals("a &amp; &lt;b&gt;", XmlEscape.escapeAttributeValue("a & <b>"))
    }

    @Test
    fun `empty string passes through`() {
        assertEquals("", XmlEscape.escapeText(""))
        assertEquals("", XmlEscape.escapeAttributeValue(""))
    }
}

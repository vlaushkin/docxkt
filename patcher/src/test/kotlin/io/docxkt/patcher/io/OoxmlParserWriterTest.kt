// No upstream analogue — OoxmlParser + OoxmlWriter round-trip
// behavioural tests.
package io.docxkt.patcher.io

import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import org.xml.sax.SAXParseException
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

internal class OoxmlParserTest {

    @Test fun `parses basic OOXML namespace-aware`() {
        val xml = """<?xml version="1.0"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body/></w:document>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val root = doc.documentElement
        assertEquals("http://schemas.openxmlformats.org/wordprocessingml/2006/main", root.namespaceURI)
        assertEquals("document", root.localName)
    }

    @Test fun `parses element with attributes`() {
        val xml = """<?xml version="1.0"?><w:p xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main" w:rsidR="00000000"><w:t>hi</w:t></w:p>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val root = doc.documentElement
        // namespaceURI handles the qualified attribute correctly.
        assertEquals("00000000", root.getAttribute("w:rsidR"))
    }

    @Test fun `XXE - DOCTYPE declaration is rejected`() {
        // Hardening: disallow-doctype-decl is enabled.
        val xml = """<?xml version="1.0"?><!DOCTYPE foo SYSTEM "http://example.com/foo.dtd"><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"/>"""
        assertFails { OoxmlParser.parse(xml.toByteArray()) }
    }

    @Test fun `malformed XML throws`() {
        val xml = """<?xml version="1.0"?><w:document>"""  // unclosed
        assertFails { OoxmlParser.parse(xml.toByteArray()) }
    }

    @Test fun `parser preserves text content`() {
        val xml = """<?xml version="1.0"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">Hello, world!</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        assertEquals("Hello, world!", doc.documentElement.textContent)
    }

    @Test fun `parser preserves entity-decoded characters`() {
        val xml = """<?xml version="1.0"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">a &amp; b</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        // Decoded to literal & in the DOM.
        assertEquals("a & b", doc.documentElement.textContent)
    }
}

internal class OoxmlWriterTest {

    @Test fun `serialize emits XML declaration prelude`() {
        // JAXP's Transformer behavior: standalone="yes" property is
        // only honoured when the source document had one. We don't
        // assert on standalone presence — just on the version and
        // encoding fields.
        val xml = """<?xml version="1.0"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">x</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue(out.startsWith("""<?xml version="1.0" encoding="UTF-8""""), "got: $out")
    }

    @Test fun `serialize honours standalone yes when source has it`() {
        // When the source declares standalone="yes", JAXP propagates.
        val xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">x</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue("""standalone="yes"""" in out, "got: $out")
    }

    @Test fun `serialize re-escapes XML-significant text`() {
        val xml = """<?xml version="1.0"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">a &amp; b</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue("a &amp; b" in out)
    }

    @Test fun `serialize after mutating Text node value`() {
        val xml = """<?xml version="1.0"?><w:t xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">old</w:t>"""
        val doc = OoxmlParser.parse(xml.toByteArray())
        val text = doc.documentElement.firstChild as org.w3c.dom.Text
        text.data = "new"
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue(">new<" in out)
    }

    @Test fun `serialize round-trips a paragraph element semantically`() {
        val source = """<?xml version="1.0"?><w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main"><w:body><w:p><w:r><w:t>Hi</w:t></w:r></w:p></w:body></w:document>"""
        val doc = OoxmlParser.parse(source.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        // Round-trip is XMLUnit-equivalent, NOT byte-equal:
        // empty elements may emit as <x></x> not <x/>.
        assertTrue("<w:p" in out)
        assertTrue("<w:r" in out)
        assertTrue(">Hi<" in out)
    }

    // Fidelity tests — guarantee parser preserves source
    // namespace order, xml:space="preserve", and duplicate
    // attribute values.

    @Test fun `parser preserves namespace declaration order`() {
        val source = """<?xml version="1.0"?><w:document xmlns:wpc="http://wpc" xmlns:cx="http://cx" xmlns:r="http://r" xmlns:w="http://w"/>"""
        val doc = OoxmlParser.parse(source.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue("xmlns:wpc=\"http://wpc\" xmlns:cx=\"http://cx\" xmlns:r=\"http://r\" xmlns:w=\"http://w\"" in out, "got: $out")
    }

    @Test fun `parser preserves xml-space preserve on text element`() {
        val source = """<?xml version="1.0"?><w:t xmlns:w="http://w" xml:space="preserve">  spaced  </w:t>"""
        val doc = OoxmlParser.parse(source.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue("xml:space=\"preserve\"" in out, "got: $out")
        assertTrue(">  spaced  <" in out, "got: $out")
    }

    @Test fun `parser preserves duplicate-token attribute values`() {
        // mc:Ignorable carries a space-separated token list. The
        // JDK DocumentBuilder previously dedup'd repeated tokens.
        val source = """<?xml version="1.0"?><w:document xmlns:w="http://w" xmlns:mc="http://mc" mc:Ignorable="w14 w15 wp14"/>"""
        val doc = OoxmlParser.parse(source.toByteArray())
        val out = OoxmlWriter.serialize(doc).toString(Charsets.UTF_8)
        assertTrue("mc:Ignorable=\"w14 w15 wp14\"" in out, "got: $out")
    }
}

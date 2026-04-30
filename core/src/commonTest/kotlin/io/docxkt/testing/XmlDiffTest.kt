package io.docxkt.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class XmlDiffTest {

    @Test
    fun `identical XML matches`() {
        val xml = "<root><a x=\"1\"/><b/></root>"
        assertNull(XmlDiff.diff(xml, xml))
    }

    @Test
    fun `whitespace between elements is ignored`() {
        val expected = "<root><a/><b/></root>"
        val actual = "<root>\n  <a/>\n  <b/>\n</root>"
        assertNull(XmlDiff.diff(expected, actual))
    }

    @Test
    fun `attribute order does not matter`() {
        val expected = "<x a=\"1\" b=\"2\" c=\"3\"/>"
        val actual = "<x c=\"3\" a=\"1\" b=\"2\"/>"
        assertNull(XmlDiff.diff(expected, actual))
    }

    @Test
    fun `xmlns declarations are ignored from attribute set`() {
        val expected = """<root xmlns:w="urn:w" xmlns:r="urn:r"><w:a/></root>"""
        val actual = """<root xmlns:r="urn:r" xmlns:w="urn:w"><w:a/></root>"""
        assertNull(XmlDiff.diff(expected, actual))
    }

    @Test
    fun `differing attribute values are flagged`() {
        val expected = "<x a=\"1\"/>"
        val actual = "<x a=\"2\"/>"
        val msg = XmlDiff.diff(expected, actual)
        assertNotNull(msg)
        assertEquals(true, msg.contains("attribute mismatch"), "got: $msg")
        assertEquals(true, msg.contains("a='1'"), "got: $msg")
    }

    @Test
    fun `missing attribute is flagged`() {
        val expected = "<x a=\"1\" b=\"2\"/>"
        val actual = "<x a=\"1\"/>"
        val msg = XmlDiff.diff(expected, actual)
        assertNotNull(msg)
        assertEquals(true, msg.contains("missing"), "got: $msg")
    }

    @Test
    fun `child order is strict`() {
        val expected = "<root><a/><b/></root>"
        val actual = "<root><b/><a/></root>"
        val msg = XmlDiff.diff(expected, actual)
        assertNotNull(msg)
    }

    @Test
    fun `text content matters`() {
        val expected = "<x>hello</x>"
        val actual = "<x>world</x>"
        val msg = XmlDiff.diff(expected, actual)
        assertNotNull(msg)
        assertEquals(true, msg.contains("text mismatch"), "got: $msg")
    }

    @Test
    fun `nested element mismatch reports path`() {
        val expected = "<root><a><b><c x=\"1\"/></b></a></root>"
        val actual = "<root><a><b><c x=\"2\"/></b></a></root>"
        val msg = XmlDiff.diff(expected, actual)
        assertNotNull(msg)
        // Path should mention the nested location.
        assertEquals(true, msg.contains("c"), "got: $msg")
    }

    @Test
    fun `namespace prefix differences are tolerated when URIs match`() {
        val expected = """<root xmlns:w="urn:w"><w:p/></root>"""
        val actual = """<root xmlns:foo="urn:w"><foo:p/></root>"""
        // Both elements bind to namespace urn:w with localName "p" — should match.
        assertNull(XmlDiff.diff(expected, actual))
    }

    @Test
    fun `OOXML-shaped fragment matches itself`() {
        val xml = """<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<w:document xmlns:w="http://schemas.openxmlformats.org/wordprocessingml/2006/main">
    <w:body>
        <w:p><w:r><w:t xml:space="preserve">Hello World</w:t></w:r></w:p>
    </w:body>
</w:document>"""
        assertNull(XmlDiff.diff(xml, xml))
    }
}

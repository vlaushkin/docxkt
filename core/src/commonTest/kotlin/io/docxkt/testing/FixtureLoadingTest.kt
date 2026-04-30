package io.docxkt.testing

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FixtureLoadingTest {

    @Test
    fun `hello-world fixture document part loads on every target`() {
        val bytes = loadFixtureBytes("hello-world", "word/document.xml")
        assertTrue(bytes.size > 100, "expected > 100 bytes, got ${bytes.size}")
        val xml = bytes.decodeToString()
        assertTrue(xml.startsWith("<?xml"), "expected XML declaration, got: ${xml.take(40)}")
        assertTrue(xml.contains("<w:document"), "expected <w:document>, got: ${xml.take(120)}")
    }

    @Test
    fun `content-types part loads`() {
        val bytes = loadFixtureBytes("hello-world", "[Content_Types].xml")
        val xml = bytes.decodeToString()
        assertTrue(xml.contains("<Types"), "got: ${xml.take(120)}")
    }

    @Test
    fun `fixture XML round-trips through XmlDiff against itself`() {
        val xml = loadFixtureXml("hello-world", "word/document.xml")
        assertEquals(null, XmlDiff.diff(xml, xml))
    }
}

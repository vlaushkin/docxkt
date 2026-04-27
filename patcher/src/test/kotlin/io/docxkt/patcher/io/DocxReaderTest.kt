// No upstream analogue — DocxReader behavioural tests.
package io.docxkt.patcher.io

import io.docxkt.pack.DocxPackager
import org.junit.jupiter.api.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class DocxReaderTest {

    @Test fun `read preserves entry contents byte-for-byte`() {
        val docXml = "<w:document/>".toByteArray()
        val ctXml = "<Types/>".toByteArray()
        val zipped = DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", docXml),
            DocxPackager.Entry("[Content_Types].xml", ctXml),
        ))
        val parts = DocxReader.read(zipped)
        assertContentEquals(docXml, parts.getValue("word/document.xml"))
        assertContentEquals(ctXml, parts.getValue("[Content_Types].xml"))
    }

    @Test fun `read returns LinkedHashMap preserving order`() {
        // DocxPackager re-orders by bucket; the read should reflect
        // that order. Two entries in different buckets test the
        // packager → reader pipeline.
        val zipped = DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", "a".toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "b".toByteArray()),
        ))
        val parts = DocxReader.read(zipped)
        // [Content_Types].xml is bucket 1, word/document.xml is bucket 3 →
        // content-types should appear first in iteration order.
        val keys = parts.keys.toList()
        assertEquals("[Content_Types].xml", keys[0])
        assertEquals("word/document.xml", keys[1])
    }

    @Test fun `empty ZIP returns empty map`() {
        val zipped = DocxPackager.toByteArray(emptyList())
        val parts = DocxReader.read(zipped)
        assertTrue(parts.isEmpty())
    }

    @Test fun `read of single-entry ZIP`() {
        val zipped = DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/document.xml", "hello".toByteArray()),
        ))
        val parts = DocxReader.read(zipped)
        assertEquals(1, parts.size)
        assertEquals("hello", parts.getValue("word/document.xml").toString(Charsets.UTF_8))
    }

    @Test fun `read of binary entries preserves bytes`() {
        val pngHeader = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A)
        val zipped = DocxPackager.toByteArray(listOf(
            DocxPackager.Entry("word/media/image1.png", pngHeader),
        ))
        val parts = DocxReader.read(zipped)
        assertContentEquals(pngHeader, parts.getValue("word/media/image1.png"))
    }
}

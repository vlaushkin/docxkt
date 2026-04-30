package io.docxkt.pack

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ZipReaderTest {

    @Test
    fun `round-trips entries written by the framer`() {
        val entries = listOf(
            DocxPackager.Entry("word/document.xml", "<doc/>".encodeToByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "<Types/>".encodeToByteArray()),
            DocxPackager.Entry("_rels/.rels", "<Relationships/>".encodeToByteArray()),
        )
        val zipped = DocxPackager.toByteArray(entries)

        val read = ZipReader.read(zipped)
        assertEquals(entries.size, read.size)
        for (entry in entries) {
            val actual = read[entry.path] ?: error("missing entry: ${entry.path}")
            assertContentEquals(entry.bytes, actual, "bytes mismatch for ${entry.path}")
        }
    }

    @Test
    fun `preserves source order of entries`() {
        val entries = listOf(
            DocxPackager.Entry("_rels/.rels", "a".encodeToByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "b".encodeToByteArray()),
            DocxPackager.Entry("word/_rels/document.xml.rels", "c".encodeToByteArray()),
            DocxPackager.Entry("word/document.xml", "d".encodeToByteArray()),
        )
        val zipped = DocxPackager.toByteArray(entries)
        val read = ZipReader.read(zipped)

        // Order matches the framer's EntryOrder, NOT the input order.
        val expectedOrder = listOf(
            "_rels/.rels",
            "[Content_Types].xml",
            "word/_rels/document.xml.rels",
            "word/document.xml",
        )
        assertEquals(expectedOrder, read.keys.toList())
    }

    @Test
    fun `inflates large entries correctly`() {
        // 16 KiB of repetitive text — DEFLATE should compress significantly,
        // and INFLATE must restore the bytes exactly.
        val payload = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. ".repeat(280)
        val bytes = payload.encodeToByteArray()
        val entries = listOf(DocxPackager.Entry("word/document.xml", bytes))
        val zipped = DocxPackager.toByteArray(entries)

        val read = ZipReader.read(zipped)
        assertContentEquals(bytes, read.getValue("word/document.xml"))
        // The compressed ZIP envelope should be smaller than the payload
        // (16 KiB compresses to under 200 bytes for repetitive text).
        assertTrue(zipped.size < bytes.size / 2, "zipped ${zipped.size} >= bytes/2 ${bytes.size / 2}")
    }
}

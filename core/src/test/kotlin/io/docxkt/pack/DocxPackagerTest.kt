package io.docxkt.pack

import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

internal class DocxPackagerTest {

    @Test
    fun `writes entries and round-trips their bytes`() {
        val entries = listOf(
            DocxPackager.Entry("word/document.xml", "<doc/>".toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "<types/>".toByteArray()),
            DocxPackager.Entry("_rels/.rels", "<rels/>".toByteArray()),
        )

        val zipped = DocxPackager.toByteArray(entries)
        val read = readZip(zipped)

        assertEquals(3, read.size)
        assertEquals("<rels/>", String(read.getValue("_rels/.rels")))
        assertEquals("<types/>", String(read.getValue("[Content_Types].xml")))
        assertEquals("<doc/>", String(read.getValue("word/document.xml")))
    }

    @Test
    fun `entry order is stable and matches architecture spec`() {
        // Given in arbitrary order; packager should sort to: rels, ContentTypes,
        // word/_rels/*, word/*, docProps/*, word/media/*.
        val entries = listOf(
            DocxPackager.Entry("word/media/image1.png", byteArrayOf(1)),
            DocxPackager.Entry("docProps/app.xml", "app".toByteArray()),
            DocxPackager.Entry("word/document.xml", "d".toByteArray()),
            DocxPackager.Entry("word/_rels/document.xml.rels", "r".toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "t".toByteArray()),
            DocxPackager.Entry("_rels/.rels", "pr".toByteArray()),
        )

        val zipped = DocxPackager.toByteArray(entries)
        val paths = readZipOrder(zipped)

        assertContentEquals(
            listOf(
                "_rels/.rels",
                "[Content_Types].xml",
                "word/_rels/document.xml.rels",
                "word/document.xml",
                "docProps/app.xml",
                "word/media/image1.png",
            ),
            paths,
        )
    }

    @Test
    fun `output is deterministic for identical inputs`() {
        val entries = listOf(
            DocxPackager.Entry("word/document.xml", "x".toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "t".toByteArray()),
            DocxPackager.Entry("_rels/.rels", "r".toByteArray()),
        )
        val a = DocxPackager.toByteArray(entries)
        val b = DocxPackager.toByteArray(entries)
        assertContentEquals(a, b)
    }

    private fun readZip(bytes: ByteArray): Map<String, ByteArray> {
        val result = LinkedHashMap<String, ByteArray>()
        ZipInputStream(ByteArrayInputStream(bytes)).use { zin ->
            while (true) {
                val entry = zin.nextEntry ?: break
                result[entry.name] = zin.readAllBytes()
                zin.closeEntry()
            }
        }
        return result
    }

    private fun readZipOrder(bytes: ByteArray): List<String> = readZip(bytes).keys.toList()
}

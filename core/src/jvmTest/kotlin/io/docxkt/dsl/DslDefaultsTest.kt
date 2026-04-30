// No upstream analogue — synthetic checks pinning the DSL's
// default-state behaviour.
package io.docxkt.dsl

import io.docxkt.api.document
import io.docxkt.api.toByteArray
import java.util.zip.ZipInputStream
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Pins the DSL's "minimal document" defaults: a `document {}` with
 * a single paragraph emits the upstream-compatible part set, and
 * unset DSL fields produce no extra wire output. These properties
 * are easy to break with overzealous refactors; this suite catches
 * such regressions independent of the byte-equal fixture diffs.
 */
internal class DslDefaultsTest {

    @Test
    fun `minimal document emits the expected ZIP entries`() {
        val bytes = document { paragraph { text("hi") } }.toByteArray()
        val entries = entriesOf(bytes)
        assertTrue("[Content_Types].xml" in entries)
        assertTrue("_rels/.rels" in entries)
        assertTrue("word/document.xml" in entries)
        assertTrue("word/_rels/document.xml.rels" in entries)
        assertTrue("word/settings.xml" in entries)
    }

    @Test
    fun `paragraph with no formatting emits no pPr`() {
        val xml = documentXml { paragraph { text("plain") } }
        assertFalse(xml.contains("<w:pPr"), "unset paragraph properties should not emit <w:pPr>")
    }

    @Test
    fun `run with no formatting emits no rPr`() {
        val xml = documentXml { paragraph { text("plain") } }
        assertFalse(xml.contains("<w:rPr"), "unset run properties should not emit <w:rPr>")
    }

    @Test
    fun `numbering and styles parts are absent without DSL hooks`() {
        val entries = entriesOf(document { paragraph { text("hi") } }.toByteArray())
        assertFalse("word/numbering.xml" in entries)
        assertFalse("word/styles.xml" in entries)
    }

    @Test
    fun `numbering part appears once a listTemplate is declared`() {
        val bytes = document {
            listTemplate("ordered") {
                level(level = 0, format = io.docxkt.model.numbering.LevelFormat.DECIMAL, text = "%1.")
            }
            paragraph {
                numbering(reference = "ordered", level = 0)
                text("first")
            }
        }.toByteArray()
        val entries = entriesOf(bytes)
        assertTrue("word/numbering.xml" in entries)
    }

    @Test
    fun `every emitted XML part starts with an XML declaration`() {
        val bytes = document { paragraph { text("hi") } }.toByteArray()
        for ((path, content) in entriesOf(bytes)) {
            if (path.endsWith(".xml") || path.endsWith(".rels")) {
                val xml = content.toString(Charsets.UTF_8)
                assertTrue(
                    xml.startsWith("<?xml"),
                    "$path should start with <?xml declaration; got: ${xml.take(40)}",
                )
            }
        }
    }

    private fun documentXml(configure: io.docxkt.dsl.DocumentScope.() -> Unit): String {
        val bytes = document(configure).toByteArray()
        return entriesOf(bytes).getValue("word/document.xml").toString(Charsets.UTF_8)
    }

    private fun entriesOf(bytes: ByteArray): Map<String, ByteArray> {
        val map = LinkedHashMap<String, ByteArray>()
        ZipInputStream(bytes.inputStream()).use { zin ->
            while (true) {
                val e = zin.nextEntry ?: break
                if (!e.isDirectory) map[e.name] = zin.readAllBytes()
            }
        }
        return map
    }
}

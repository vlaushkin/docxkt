// No upstream analogue — synthetic emission tests for MainDocumentPart.
package io.docxkt.part

import io.docxkt.model.Body
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MainDocumentPartTest {

    @Test
    fun `path matches OOXML convention`() {
        assertEquals("word/document.xml", MainDocumentPart(Body(emptyList(), sectionProperties = null)).path)
    }

    @Test
    fun `empty body emits self-closing w body`() {
        val xml = render(MainDocumentPart(Body(emptyList(), sectionProperties = null)))
        assertTrue(xml.contains("<w:body/>"))
    }

    @Test
    fun `root element carries DOCUMENT_ROOT_NAMESPACES`() {
        val xml = render(MainDocumentPart(Body(emptyList(), sectionProperties = null)))
        assertTrue(xml.contains("xmlns:w="))
        assertTrue(xml.contains("xmlns:wp="))
        assertTrue(xml.contains("xmlns:w14="))
        assertTrue(xml.contains("xmlns:w15="))
        assertTrue(xml.contains("mc:Ignorable=\"w14 w15 wp14\""))
    }

    @Test
    fun `backgroundColor emits before body`() {
        val xml = render(
            MainDocumentPart(
                body = Body(emptyList(), sectionProperties = null),
                backgroundColor = "FFCC00",
            ),
        )
        val bgIdx = xml.indexOf("<w:background ")
        val bodyIdx = xml.indexOf("<w:body")
        assertTrue(bgIdx in 0 until bodyIdx)
        assertTrue(xml.contains("w:color=\"FFCC00\""))
    }

    @Test
    fun `null backgroundColor suppresses element`() {
        val xml = render(MainDocumentPart(Body(emptyList(), sectionProperties = null)))
        assertFalse(xml.contains("<w:background"))
    }

    private fun render(part: MainDocumentPart): String =
        StringBuilder().apply { part.appendXml(this) }.toString()
}

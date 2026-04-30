// No upstream analogue — synthetic emission tests for the part-class
// layer covered indirectly via fixture diffs.
package io.docxkt.part

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ContentTypesPartTest {

    @Test
    fun `default emits base rels + xml + main-document override`() {
        val xml = render(ContentTypesPart())
        assertTrue(xml.contains("<Default ContentType=\"${ContentTypesPart.CONTENT_TYPE_RELS}\" Extension=\"rels\"/>"))
        assertTrue(xml.contains("<Default ContentType=\"${ContentTypesPart.CONTENT_TYPE_XML}\" Extension=\"xml\"/>"))
        assertTrue(
            xml.contains(
                "<Override ContentType=\"${ContentTypesPart.CONTENT_TYPE_MAIN_DOCUMENT}\" PartName=\"/word/document.xml\"/>",
            ),
        )
    }

    @Test
    fun `extra defaults emit before base defaults`() {
        val xml = render(
            ContentTypesPart(
                extraDefaults = listOf(
                    ContentTypesPart.Default("png", "image/png"),
                ),
            ),
        )
        val pngIdx = xml.indexOf("Extension=\"png\"")
        val relsIdx = xml.indexOf("Extension=\"rels\"")
        assertTrue(pngIdx in 0 until relsIdx, "png Default should precede rels Default; got pngIdx=$pngIdx relsIdx=$relsIdx")
    }

    @Test
    fun `extra overrides emit after main-document override`() {
        val xml = render(
            ContentTypesPart(
                extraOverrides = listOf(
                    ContentTypesPart.Override("/word/header1.xml", ContentTypesPart.CONTENT_TYPE_HEADER),
                ),
            ),
        )
        val mainIdx = xml.indexOf("/word/document.xml")
        val hdrIdx = xml.indexOf("/word/header1.xml")
        assertTrue(mainIdx in 0 until hdrIdx)
    }

    @Test
    fun `path is fixed at root manifest`() {
        assertEquals("[Content_Types].xml", ContentTypesPart().path)
    }

    @Test
    fun `output is parseable XML with declaration`() {
        val xml = render(ContentTypesPart())
        assertTrue(xml.startsWith("<?xml"))
        assertTrue(xml.contains("xmlns="))
    }

    private fun render(part: ContentTypesPart): String =
        StringBuilder().apply { part.appendXml(this) }.toString()
}

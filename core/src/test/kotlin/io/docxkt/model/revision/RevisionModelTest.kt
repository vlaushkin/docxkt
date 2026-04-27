// No upstream analogue — synthetic tests for model/revision.
package io.docxkt.model.revision

import io.docxkt.model.paragraph.run.Run
import io.docxkt.model.paragraph.run.Text
import io.docxkt.xml.openElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class RevisionModelTest {

    @Test
    fun `RevisionInfo exposes attributes in upstream order`() {
        val info = RevisionInfo(id = 4, author = "Vasily", date = "2026-04-27T10:00:00Z")
        assertEquals(
            listOf(
                "w:id" to "4",
                "w:author" to "Vasily",
                "w:date" to "2026-04-27T10:00:00Z",
            ),
            info.attrs().toList(),
        )
    }

    @Test
    fun `RevisionInfo wrapping element escapes attribute values`() {
        val info = RevisionInfo(id = 1, author = "A&B", date = "now")
        val out = StringBuilder()
        out.openElement("w:rPrChange", *info.attrs())
        assertTrue(out.contains("A&amp;B"))
    }

    @Test
    fun `InsertedRun wraps runs verbatim`() {
        val ins = InsertedRun(
            id = 1,
            author = "Vasily",
            date = "2026-04-27T10:00:00Z",
            runs = listOf(Run(children = listOf(Text("hello")))),
        )
        val xml = render(ins)
        assertTrue(xml.startsWith("<w:ins w:id=\"1\""))
        assertTrue(xml.contains("w:author=\"Vasily\""))
        assertTrue(xml.contains("w:date=\"2026-04-27T10:00:00Z\""))
        assertTrue(xml.contains("<w:r><w:t xml:space=\"preserve\">hello</w:t></w:r>"))
        assertTrue(xml.endsWith("</w:ins>"))
    }

    @Test
    fun `DeletedRun rewrites w t children to w delText`() {
        val del = DeletedRun(
            id = 2,
            author = "Vasily",
            date = "2026-04-27T10:00:00Z",
            runs = listOf(Run(children = listOf(Text("removed")))),
        )
        val xml = render(del)
        assertTrue(xml.startsWith("<w:del w:id=\"2\""))
        assertTrue(xml.contains("<w:delText"))
        assertTrue(xml.contains("removed"))
        assertTrue(xml.endsWith("</w:del>"))
    }

    @Test
    fun `DeletedText emits with xml space preserve`() {
        val xml = render(DeletedText(value = "  spaced  "))
        assertTrue(xml.contains("xml:space=\"preserve\""))
        assertTrue(xml.contains("  spaced  "))
    }

    @Test
    fun `DeletedInstrText emits inside delInstrText element`() {
        val xml = render(DeletedInstrText(instruction = "PAGE \\* MERGEFORMAT"))
        assertTrue(xml.startsWith("<w:delInstrText xml:space=\"preserve\""))
        assertTrue(xml.contains("PAGE"))
        assertTrue(xml.endsWith("</w:delInstrText>"))
    }

    private fun render(component: io.docxkt.xml.XmlComponent): String =
        StringBuilder().apply { component.appendXml(this) }.toString()
}

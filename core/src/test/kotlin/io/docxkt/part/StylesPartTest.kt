// No upstream analogue — synthetic emission tests for StylesPart.
package io.docxkt.part

import io.docxkt.model.paragraph.run.RunProperties
import io.docxkt.model.style.Style
import io.docxkt.model.style.StyleType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StylesPartTest {

    @Test
    fun `empty StylesPart is reported as empty`() {
        assertFalse(StylesPart(emptyList()).isNonEmpty)
    }

    @Test
    fun `paragraph style emits in the expected wire shape`() {
        val style = Style(
            type = StyleType.PARAGRAPH,
            id = "Heading1",
            name = "heading 1",
            uiPriority = 9,
            quickFormat = true,
        )
        val xml = render(StylesPart(listOf(style)))
        assertTrue(xml.contains("<w:style w:type=\"paragraph\" w:styleId=\"Heading1\">"))
        assertTrue(xml.contains("<w:name w:val=\"heading 1\"/>"))
        assertTrue(xml.contains("<w:uiPriority w:val=\"9\"/>"))
        assertTrue(xml.contains("<w:qFormat/>"))
    }

    @Test
    fun `character style emits run properties when set`() {
        val style = Style(
            type = StyleType.CHARACTER,
            id = "Strong",
            runProperties = RunProperties(bold = true),
        )
        val xml = render(StylesPart(listOf(style)))
        assertTrue(xml.contains("<w:style w:type=\"character\" w:styleId=\"Strong\">"))
        assertTrue(xml.contains("<w:rPr>"))
        assertTrue(xml.contains("<w:b/>"))
    }

    @Test
    fun `path matches OOXML convention`() {
        assertEquals("word/styles.xml", StylesPart(emptyList()).path)
    }

    @Test
    fun `root element carries narrow 5-namespace styles set`() {
        val xml = render(StylesPart(listOf(Style(type = StyleType.PARAGRAPH, id = "x"))))
        // Spot-check namespaces upstream emits on <w:styles>.
        assertTrue(xml.contains("xmlns:w="))
        assertTrue(xml.contains("xmlns:w14="))
        assertTrue(xml.contains("xmlns:w15="))
        assertTrue(xml.contains("xmlns:r="))
        assertTrue(xml.contains("xmlns:mc="))
        // Narrow set — no wp/v/o.
        assertFalse(xml.contains("xmlns:wp="))
    }

    private fun render(part: StylesPart): String =
        StringBuilder().apply { part.appendXml(this) }.toString()
}

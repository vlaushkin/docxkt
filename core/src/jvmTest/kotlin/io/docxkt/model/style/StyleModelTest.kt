// No upstream analogue — synthetic tests for model/style.
package io.docxkt.model.style

import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.run.RunProperties
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class StyleModelTest {

    @Test
    fun `StyleType wire tokens match upstream`() {
        assertEquals("paragraph", StyleType.PARAGRAPH.wire)
        assertEquals("character", StyleType.CHARACTER.wire)
    }

    @Test
    fun `Style emits w type and w styleId attributes`() {
        val style = Style(type = StyleType.PARAGRAPH, id = "Heading1")
        val xml = render(style)
        assertTrue(xml.startsWith("<w:style w:type=\"paragraph\" w:styleId=\"Heading1\">"))
    }

    @Test
    fun `Style emits child elements in canonical order`() {
        val style = Style(
            type = StyleType.PARAGRAPH,
            id = "MyStyle",
            name = "My Style",
            basedOn = "Normal",
            next = "Normal",
            link = "MyStyleChar",
            uiPriority = 9,
            semiHidden = true,
            unhideWhenUsed = true,
            quickFormat = true,
        )
        val xml = render(style)
        val nameIdx = xml.indexOf("<w:name ")
        val basedOnIdx = xml.indexOf("<w:basedOn ")
        val nextIdx = xml.indexOf("<w:next ")
        val linkIdx = xml.indexOf("<w:link ")
        val uiPrioIdx = xml.indexOf("<w:uiPriority ")
        val semiHIdx = xml.indexOf("<w:semiHidden")
        val unhideIdx = xml.indexOf("<w:unhideWhenUsed")
        val qFormatIdx = xml.indexOf("<w:qFormat")
        assertTrue(nameIdx in 0 until basedOnIdx)
        assertTrue(basedOnIdx in 0 until nextIdx)
        assertTrue(nextIdx in 0 until linkIdx)
        assertTrue(linkIdx in 0 until uiPrioIdx)
        assertTrue(uiPrioIdx in 0 until semiHIdx)
        assertTrue(semiHIdx in 0 until unhideIdx)
        assertTrue(unhideIdx in 0 until qFormatIdx)
    }

    @Test
    fun `Style omits unset optional fields`() {
        val xml = render(Style(type = StyleType.CHARACTER, id = "X"))
        assertFalse(xml.contains("<w:name"))
        assertFalse(xml.contains("<w:basedOn"))
        assertFalse(xml.contains("<w:uiPriority"))
        assertFalse(xml.contains("<w:qFormat"))
    }

    @Test
    fun `DocumentDefaults always emits rPrDefault and pPrDefault wrappers`() {
        val xml = render(DocumentDefaults())
        assertTrue(xml.contains("<w:docDefaults>"))
        assertTrue(xml.contains("<w:rPrDefault>"))
        assertTrue(xml.contains("<w:pPrDefault>"))
        assertTrue(xml.endsWith("</w:docDefaults>"))
    }

    @Test
    fun `DocumentDefaults emits inner properties when set`() {
        val dd = DocumentDefaults(
            runDefaults = RunProperties(bold = true),
            paragraphDefaults = ParagraphProperties(),
        )
        val xml = render(dd)
        assertTrue(xml.contains("<w:rPrDefault>"))
        assertTrue(xml.contains("<w:rPr>"))
        assertTrue(xml.contains("<w:b/>"))
    }

    private fun render(component: io.docxkt.xml.XmlComponent): String =
        StringBuilder().apply { component.appendXml(this) }.toString()
}

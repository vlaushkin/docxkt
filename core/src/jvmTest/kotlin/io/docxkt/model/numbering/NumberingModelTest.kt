// No upstream analogue — synthetic emission tests for model/numbering.
package io.docxkt.model.numbering

import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.Indentation
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class NumberingModelTest {

    @Test
    fun `LevelFormat enum maps every value to a non-empty wire token`() {
        for (fmt in LevelFormat.entries) {
            assertTrue(fmt.wire.isNotEmpty(), "$fmt has empty wire token")
        }
    }

    @Test
    fun `MultiLevelType enum maps every value to a non-empty wire token`() {
        for (t in MultiLevelType.entries) {
            assertTrue(t.wire.isNotEmpty(), "$t has empty wire token")
        }
    }

    @Test
    fun `NumberingLevel emits child elements in canonical order`() {
        val level = NumberingLevel(
            level = 1,
            format = LevelFormat.UPPER_LETTER,
            text = "%1.",
            start = 5,
            justification = AlignmentType.RIGHT,
        )
        val xml = render(level)
        assertTrue(xml.startsWith("<w:lvl w:ilvl=\"1\""))
        assertTrue(xml.contains("w15:tentative=\"1\""))
        val startIdx = xml.indexOf("<w:start w:val=\"5\"/>")
        val numFmtIdx = xml.indexOf("<w:numFmt w:val=\"upperLetter\"/>")
        val lvlTextIdx = xml.indexOf("<w:lvlText w:val=\"%1.\"/>")
        val lvlJcIdx = xml.indexOf("<w:lvlJc w:val=\"right\"/>")
        assertTrue(startIdx in 0 until numFmtIdx)
        assertTrue(numFmtIdx in 0 until lvlTextIdx)
        assertTrue(lvlTextIdx in 0 until lvlJcIdx)
    }

    @Test
    fun `NumberingLevel emits indent inside pPr when set`() {
        val level = NumberingLevel(
            level = 0,
            format = LevelFormat.BULLET,
            text = "•",
            indentation = Indentation(left = 720, hanging = 360),
        )
        val xml = render(level)
        assertTrue(xml.contains("<w:pPr>"))
        assertTrue(xml.contains("<w:ind"))
        assertTrue(xml.contains("w:left=\"720\""))
        assertTrue(xml.contains("w:hanging=\"360\""))
    }

    @Test
    fun `AbstractNumbering opens with abstractNumId attribute`() {
        val a = AbstractNumbering(
            abstractNumId = 4,
            levels = listOf(NumberingLevel(level = 0, format = LevelFormat.DECIMAL, text = "%1.")),
        )
        val xml = render(a)
        assertTrue(xml.contains("<w:abstractNum w:abstractNumId=\"4\""))
        assertTrue(xml.contains("w15:restartNumberingAfterBreak=\"0\""))
    }

    @Test
    fun `ConcreteNumbering emits auto level-0 startOverride`() {
        val c = ConcreteNumbering(numId = 2, abstractNumId = 4, startOverride = 7)
        val xml = render(c)
        assertTrue(xml.contains("<w:num w:numId=\"2\""))
        assertTrue(xml.contains("<w:abstractNumId w:val=\"4\"/>"))
        assertTrue(xml.contains("<w:lvlOverride w:ilvl=\"0\""))
        assertTrue(xml.contains("<w:startOverride w:val=\"7\"/>"))
    }

    @Test
    fun `DefaultBulletNumbering covers nine levels with cycling glyphs`() {
        assertEquals(9, DefaultBulletNumbering.LEVELS.size)
        for (level in DefaultBulletNumbering.LEVELS) {
            assertEquals(LevelFormat.BULLET, level.format)
        }
    }

    private fun render(component: io.docxkt.xml.XmlComponent): String =
        StringBuilder().apply { component.appendXml(this) }.toString()
}

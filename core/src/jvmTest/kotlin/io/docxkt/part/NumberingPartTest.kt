// No upstream analogue — synthetic emission tests for NumberingPart.
package io.docxkt.part

import io.docxkt.model.numbering.AbstractNumbering
import io.docxkt.model.numbering.ConcreteNumbering
import io.docxkt.model.numbering.LevelFormat
import io.docxkt.model.numbering.MultiLevelType
import io.docxkt.model.numbering.NumberingLevel
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class NumberingPartTest {

    @Test
    fun `empty NumberingPart is reported as empty`() {
        assertFalse(NumberingPart(emptyList(), emptyList()).isNonEmpty)
    }

    @Test
    fun `path matches OOXML convention`() {
        assertEquals("word/numbering.xml", NumberingPart(emptyList(), emptyList()).path)
    }

    @Test
    fun `abstract numbering emits restartNumberingAfterBreak attribute`() {
        val xml = render(
            NumberingPart(
                abstracts = listOf(
                    AbstractNumbering(
                        abstractNumId = 0,
                        levels = listOf(
                            NumberingLevel(level = 0, format = LevelFormat.DECIMAL, text = "%1."),
                        ),
                    ),
                ),
                concretes = emptyList(),
            ),
        )
        assertTrue(xml.contains("<w:abstractNum w:abstractNumId=\"0\""))
        assertTrue(xml.contains("w15:restartNumberingAfterBreak=\"0\""))
        assertTrue(xml.contains("<w:multiLevelType w:val=\"hybridMultilevel\"/>"))
    }

    @Test
    fun `numbering level emits start numFmt lvlText lvlJc in order`() {
        val xml = render(
            NumberingPart(
                abstracts = listOf(
                    AbstractNumbering(
                        abstractNumId = 0,
                        levels = listOf(
                            NumberingLevel(
                                level = 0,
                                format = LevelFormat.LOWER_ROMAN,
                                text = "%1.",
                                start = 1,
                            ),
                        ),
                    ),
                ),
                concretes = emptyList(),
            ),
        )
        val startIdx = xml.indexOf("<w:start ")
        val fmtIdx = xml.indexOf("<w:numFmt ")
        val lvlTextIdx = xml.indexOf("<w:lvlText ")
        val lvlJcIdx = xml.indexOf("<w:lvlJc ")
        assertTrue(startIdx in 0 until fmtIdx)
        assertTrue(fmtIdx in 0 until lvlTextIdx)
        assertTrue(lvlTextIdx in 0 until lvlJcIdx)
    }

    @Test
    fun `concrete numbering binds to its abstract`() {
        val xml = render(
            NumberingPart(
                abstracts = listOf(
                    AbstractNumbering(
                        abstractNumId = 7,
                        levels = listOf(
                            NumberingLevel(level = 0, format = LevelFormat.BULLET, text = "•"),
                        ),
                        multiLevelType = MultiLevelType.HYBRID_MULTILEVEL,
                    ),
                ),
                concretes = listOf(ConcreteNumbering(numId = 1, abstractNumId = 7)),
            ),
        )
        assertTrue(xml.contains("<w:num w:numId=\"1\""))
        assertTrue(xml.contains("<w:abstractNumId w:val=\"7\"/>"))
    }

    private fun render(part: NumberingPart): String =
        StringBuilder().apply { part.appendXml(this) }.toString()
}

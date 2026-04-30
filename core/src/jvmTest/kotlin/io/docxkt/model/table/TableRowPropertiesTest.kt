// No upstream analogue — TableRowProperties matrix.
package io.docxkt.model.table

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TableRowPropertiesTest {

    private fun render(p: TableRowProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    @Test fun `all-null emits empty string`() {
        assertEquals("", render(TableRowProperties()))
    }

    @Test fun `tableHeader null emits no tblHeader element`() {
        assertEquals("", render(TableRowProperties(tableHeader = null)))
    }

    @Test fun `tableHeader true emits self-closing w tblHeader`() {
        val xml = render(TableRowProperties(tableHeader = true))
        assertTrue("<w:tblHeader/>" in xml)
    }

    @Test fun `tableHeader false emits w tblHeader val false`() {
        val xml = render(TableRowProperties(tableHeader = false))
        assertTrue("""<w:tblHeader w:val="false"/>""" in xml)
    }

    @Test fun `height value only emits w trHeight with val attr`() {
        val xml = render(TableRowProperties(height = TableRowHeight(value = 720)))
        assertTrue("""<w:trHeight w:val="720"/>""" in xml)
    }

    @Test fun `height with rule EXACT emits w hRule attr`() {
        val xml = render(TableRowProperties(height = TableRowHeight(value = 720, rule = HeightRule.EXACT)))
        assertTrue("""<w:trHeight w:val="720" w:hRule="exact"/>""" in xml)
    }

    @Test fun `height with rule ATLEAST emits w hRule atLeast`() {
        val xml = render(TableRowProperties(height = TableRowHeight(value = 500, rule = HeightRule.ATLEAST)))
        assertTrue("""w:hRule="atLeast"""" in xml)
    }

    @Test fun `height with rule AUTO emits w hRule auto`() {
        val xml = render(TableRowProperties(height = TableRowHeight(value = 0, rule = HeightRule.AUTO)))
        assertTrue("""w:hRule="auto"""" in xml)
    }

    @Test fun `tableHeader precedes height in canonical order`() {
        val xml = render(TableRowProperties(tableHeader = true, height = TableRowHeight(value = 100)))
        assertTrue(xml.indexOf("<w:tblHeader") < xml.indexOf("<w:trHeight"))
    }

    @Test fun `wrapper boundaries`() {
        val xml = render(TableRowProperties(tableHeader = true))
        assertTrue(xml.startsWith("<w:trPr>"))
        assertTrue(xml.endsWith("</w:trPr>"))
    }
}

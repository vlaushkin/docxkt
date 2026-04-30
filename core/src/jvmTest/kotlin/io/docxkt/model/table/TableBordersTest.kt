// No upstream analogue — TableBorders / TableCellBorders matrix.
package io.docxkt.model.table

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.BorderStyle
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TableBordersTest {

    private fun render(b: TableBorders): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `DEFAULTS emits all six sides with upstream-default style`() {
        val xml = render(TableBorders.DEFAULTS)
        for (side in listOf("top", "left", "bottom", "right", "insideH", "insideV")) {
            assertTrue("<w:$side " in xml)
        }
    }

    @Test fun `DEFAULTS emits w val single on every side`() {
        // BorderSide.UPSTREAM_DEFAULT.style == SINGLE.
        val xml = render(TableBorders.DEFAULTS)
        // 6 occurrences of w:val="single" (one per side).
        val count = """w:val="single"""".toRegex().findAll(xml).count()
        assertEquals(6, count)
    }

    @Test fun `DEFAULTS emits w sz 4 on every side`() {
        val xml = render(TableBorders.DEFAULTS)
        val count = """w:sz="4"""".toRegex().findAll(xml).count()
        assertEquals(6, count)
    }

    @Test fun `caller-set top with custom color overrides default`() {
        val xml = render(TableBorders(top = BorderSide(style = BorderStyle.DOUBLE, color = "FF0000")))
        // Top has the custom values; other sides have defaults.
        assertTrue("""<w:top w:val="double" w:color="FF0000"""" in xml)
        // Other sides emit with auto color.
        val autoCount = """w:color="auto"""".toRegex().findAll(xml).count()
        assertEquals(5, autoCount)
    }

    @Test fun `wrapper boundaries`() {
        val xml = render(TableBorders.DEFAULTS)
        assertTrue(xml.startsWith("<w:tblBorders>"))
        assertTrue(xml.endsWith("</w:tblBorders>"))
    }

    @Test fun `every side emits in canonical order`() {
        val xml = render(TableBorders.DEFAULTS)
        val order = listOf("<w:top ", "<w:left ", "<w:bottom ", "<w:right ", "<w:insideH ", "<w:insideV ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe")
            prev = idx
        }
    }
}

internal class TableCellBordersTest {

    private fun render(b: TableCellBorders): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `all-null is suppressed`() {
        assertEquals("", render(TableCellBorders()))
    }

    @Test fun `top alone emits only top side`() {
        val xml = render(TableCellBorders(top = BorderSide()))
        assertTrue("<w:top " in xml)
        for (other in listOf("start", "left", "bottom", "end", "right")) {
            assertFalse("<w:$other " in xml, "expected no $other; got $xml")
        }
    }

    @Test fun `start side emitted before left when both set`() {
        val xml = render(TableCellBorders(start = BorderSide(), left = BorderSide()))
        assertTrue(xml.indexOf("<w:start ") < xml.indexOf("<w:left "))
    }

    @Test fun `end side emitted before right when both set`() {
        val xml = render(TableCellBorders(end = BorderSide(), right = BorderSide()))
        assertTrue(xml.indexOf("<w:end ") < xml.indexOf("<w:right "))
    }

    @Test fun `all six sides emit in upstream order top-start-left-bottom-end-right`() {
        val xml = render(TableCellBorders(
            top = BorderSide(),
            start = BorderSide(),
            left = BorderSide(),
            bottom = BorderSide(),
            end = BorderSide(),
            right = BorderSide(),
        ))
        val order = listOf("<w:top ", "<w:start ", "<w:left ", "<w:bottom ", "<w:end ", "<w:right ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe")
            prev = idx
        }
    }

    @Test fun `wrapper boundaries when non-empty`() {
        val xml = render(TableCellBorders(top = BorderSide()))
        assertTrue(xml.startsWith("<w:tcBorders>"))
        assertTrue(xml.endsWith("</w:tcBorders>"))
    }
}

internal class TableWidthFactoriesTest {

    @Test fun `auto factory defaults to size 0 type AUTO`() {
        val w = TableWidth.auto()
        assertEquals(0, w.size)
        assertEquals(WidthType.AUTO, w.type)
    }

    @Test fun `auto factory accepts explicit size`() {
        val w = TableWidth.auto(120)
        assertEquals(120, w.size)
        assertEquals(WidthType.AUTO, w.type)
    }

    @Test fun `dxa factory sets type DXA`() {
        val w = TableWidth.dxa(5000)
        assertEquals(5000, w.size)
        assertEquals(WidthType.DXA, w.type)
    }

    @Test fun `pct factory sets type PCT`() {
        val w = TableWidth.pct(2500)
        assertEquals(2500, w.size)
        assertEquals(WidthType.PCT, w.type)
    }

    @Test fun `nil factory sets type NIL with size 0`() {
        val w = TableWidth.nilType()
        assertEquals(0, w.size)
        assertEquals(WidthType.NIL_TYPE, w.type)
    }

    @Test fun `default constructor type is AUTO`() {
        // The data class's default `type` is AUTO.
        val w = TableWidth(0)
        assertEquals(WidthType.AUTO, w.type)
    }
}

internal class CellMarginsTest {

    @Test fun `empty CellMargins isEmpty true`() {
        assertTrue(CellMargins().isEmpty())
    }

    @Test fun `single side makes isEmpty false`() {
        assertFalse(CellMargins(top = 100).isEmpty())
    }

    @Test fun `unit defaults to DXA`() {
        assertEquals(WidthType.DXA, CellMargins().unit)
    }

    @Test fun `writeCellMargins skips when empty`() {
        val out = StringBuilder()
        writeCellMargins(out, "w:tcMar", CellMargins())
        assertEquals("", out.toString())
    }

    @Test fun `writeCellMargins with all sides emits expected order top-left-bottom-right`() {
        val out = StringBuilder()
        writeCellMargins(out, "w:tblCellMar", CellMargins(top = 1, left = 2, bottom = 3, right = 4))
        val xml = out.toString()
        assertTrue(xml.startsWith("<w:tblCellMar>"))
        assertTrue(xml.endsWith("</w:tblCellMar>"))
        val order = listOf("""w:w="1"""", """w:w="2"""", """w:w="3"""", """w:w="4"""")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "expected $probe in order")
            prev = idx
        }
    }

    @Test fun `writeCellMargins with PCT unit emits percent`() {
        val out = StringBuilder()
        writeCellMargins(out, "w:tcMar", CellMargins(top = 50, unit = WidthType.PCT))
        assertTrue("""<w:top w:type="pct" w:w="50%"/>""" in out.toString())
    }
}

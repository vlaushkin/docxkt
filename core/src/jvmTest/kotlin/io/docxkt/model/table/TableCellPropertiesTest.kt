// No upstream analogue — TableCellProperties matrix.
package io.docxkt.model.table

import io.docxkt.model.border.BorderSide
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TableCellPropertiesTest {

    private fun render(p: TableCellProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    // --- empty suppression ----------------------------------------------

    @Test fun `all-null emits empty string`() {
        assertEquals("", render(TableCellProperties()))
    }

    // --- width (tcW) ----------------------------------------------------

    @Test fun `width DXA emits w tcW with twips`() {
        val xml = render(TableCellProperties(width = TableWidth.dxa(2000)))
        assertTrue("""<w:tcW w:type="dxa" w:w="2000"/>""" in xml)
    }

    @Test fun `width PCT emits percent suffix`() {
        val xml = render(TableCellProperties(width = TableWidth.pct(2500)))
        assertTrue("""<w:tcW w:type="pct" w:w="2500%"/>""" in xml)
    }

    // --- gridSpan -------------------------------------------------------

    @Test fun `gridSpan emits w gridSpan val`() {
        val xml = render(TableCellProperties(gridSpan = 3))
        assertTrue("""<w:gridSpan w:val="3"/>""" in xml)
    }

    @Test fun `gridSpan one emits val 1 verbatim - no validation`() {
        // Pass-through: even gridSpan=1 (which has no merging effect)
        // emits.
        val xml = render(TableCellProperties(gridSpan = 1))
        assertTrue("""<w:gridSpan w:val="1"/>""" in xml)
    }

    // --- verticalMerge --------------------------------------------------

    @Test fun `verticalMerge RESTART emits val restart`() {
        val xml = render(TableCellProperties(verticalMerge = VerticalMerge.RESTART))
        assertTrue("""<w:vMerge w:val="restart"/>""" in xml)
    }

    @Test fun `verticalMerge CONTINUE emits val continue`() {
        val xml = render(TableCellProperties(verticalMerge = VerticalMerge.CONTINUE))
        assertTrue("""<w:vMerge w:val="continue"/>""" in xml)
    }

    // --- borders --------------------------------------------------------

    @Test fun `borders empty is suppressed`() {
        // TableCellBorders all-null → IgnoreIfEmpty skips.
        val xml = render(TableCellProperties(borders = TableCellBorders()))
        assertFalse("<w:tcBorders" in xml)
    }

    @Test fun `borders top only emits w tcBorders with top side`() {
        val xml = render(TableCellProperties(borders = TableCellBorders(top = BorderSide())))
        assertTrue("<w:tcBorders>" in xml)
        assertTrue("<w:top " in xml)
    }

    @Test fun `borders side order top-start-left-bottom-end-right`() {
        val xml = render(TableCellProperties(borders = TableCellBorders(
            top = BorderSide(),
            start = BorderSide(),
            left = BorderSide(),
            bottom = BorderSide(),
            end = BorderSide(),
            right = BorderSide(),
        )))
        val order = listOf("<w:top ", "<w:start ", "<w:left ", "<w:bottom ", "<w:end ", "<w:right ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe; got: $xml")
            prev = idx
        }
    }

    // --- shading --------------------------------------------------------

    @Test fun `shading emits w shd`() {
        val xml = render(TableCellProperties(shading = Shading(pattern = ShadingPattern.SOLID, fill = "FF0000")))
        assertTrue("<w:shd " in xml)
        assertTrue("""w:val="solid"""" in xml)
    }

    // --- margins (tcMar) ------------------------------------------------

    @Test fun `margins all-null is suppressed`() {
        val xml = render(TableCellProperties(width = TableWidth.dxa(1), margins = CellMargins()))
        assertFalse("<w:tcMar" in xml)
    }

    @Test fun `margins one side emits tcMar`() {
        val xml = render(TableCellProperties(margins = CellMargins(top = 100)))
        assertTrue("<w:tcMar>" in xml)
    }

    // --- vAlign ---------------------------------------------------------

    @Test fun `verticalAlign TOP emits val top`() {
        val xml = render(TableCellProperties(verticalAlign = VerticalAlignment.TOP))
        assertTrue("""<w:vAlign w:val="top"/>""" in xml)
    }

    @Test fun `verticalAlign CENTER emits val center`() {
        val xml = render(TableCellProperties(verticalAlign = VerticalAlignment.CENTER))
        assertTrue("""<w:vAlign w:val="center"/>""" in xml)
    }

    @Test fun `verticalAlign BOTTOM emits val bottom`() {
        val xml = render(TableCellProperties(verticalAlign = VerticalAlignment.BOTTOM))
        assertTrue("""<w:vAlign w:val="bottom"/>""" in xml)
    }

    // --- canonical child order ------------------------------------------

    @Test fun `tcPr child order is tcW-gridSpan-vMerge-tcBorders-shd-tcMar-vAlign`() {
        val xml = render(TableCellProperties(
            width = TableWidth.dxa(100),
            gridSpan = 2,
            verticalMerge = VerticalMerge.RESTART,
            borders = TableCellBorders(top = BorderSide()),
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"),
            margins = CellMargins(top = 10),
            verticalAlign = VerticalAlignment.CENTER,
        ))
        val order = listOf("<w:tcW ", "<w:gridSpan ", "<w:vMerge ", "<w:tcBorders>", "<w:shd ", "<w:tcMar>", "<w:vAlign ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe in $xml")
            prev = idx
        }
    }
}

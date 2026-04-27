// No upstream analogue — TableProperties matrix.
package io.docxkt.model.table

import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.ShadingPattern
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TablePropertiesTest {

    private fun render(p: TableProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    // --- empty suppression ----------------------------------------------

    @Test fun `all-null TableProperties emits empty string`() {
        assertEquals("", render(TableProperties()))
    }

    @Test fun `wrapper present when any single field is set`() {
        val xml = render(TableProperties(width = TableWidth.dxa(5000)))
        assertTrue(xml.startsWith("<w:tblPr>") && xml.endsWith("</w:tblPr>"))
    }

    // --- width (tblW) ---------------------------------------------------

    @Test fun `width DXA emits w tblW with type and twips value`() {
        val xml = render(TableProperties(width = TableWidth.dxa(5000)))
        assertTrue("""<w:tblW w:type="dxa" w:w="5000"/>""" in xml)
    }

    @Test fun `width PCT emits value with percent suffix`() {
        // PCT quirk: upstream emits "5000%" (fiftieths-of-percent + percent sign).
        val xml = render(TableProperties(width = TableWidth.pct(5000)))
        assertTrue("""<w:tblW w:type="pct" w:w="5000%"/>""" in xml)
    }

    @Test fun `width AUTO emits type auto`() {
        val xml = render(TableProperties(width = TableWidth.auto()))
        assertTrue("""<w:tblW w:type="auto" """ in xml)
    }

    @Test fun `width NIL emits type nil`() {
        val xml = render(TableProperties(width = TableWidth.nil()))
        assertTrue("""<w:tblW w:type="nil" """ in xml)
    }

    @Test fun `width attribute order is type then w`() {
        val xml = render(TableProperties(width = TableWidth.dxa(100)))
        val typeIdx = xml.indexOf("w:type=")
        val wIdx = xml.indexOf("w:w=")
        assertTrue(typeIdx in 0 until wIdx, "expected w:type before w:w")
    }

    // --- indent (tblInd) ------------------------------------------------

    @Test fun `indent DXA emits w tblInd`() {
        val xml = render(TableProperties(indent = TableWidth.dxa(360)))
        assertTrue("""<w:tblInd w:type="dxa" w:w="360"/>""" in xml)
    }

    // --- layout ---------------------------------------------------------

    @Test fun `layout FIXED emits w tblLayout type fixed`() {
        val xml = render(TableProperties(layout = TableLayout.FIXED))
        assertTrue("""<w:tblLayout w:type="fixed"/>""" in xml)
    }

    @Test fun `layout AUTOFIT emits w tblLayout type autofit`() {
        val xml = render(TableProperties(layout = TableLayout.AUTOFIT))
        assertTrue("""<w:tblLayout w:type="autofit"/>""" in xml)
    }

    // --- borders --------------------------------------------------------

    @Test fun `borders DEFAULTS emits all six sides as upstream-default`() {
        val xml = render(TableProperties(borders = TableBorders.DEFAULTS))
        for (side in listOf("top", "left", "bottom", "right", "insideH", "insideV")) {
            assertTrue("<w:$side " in xml, "expected $side side; got: $xml")
        }
    }

    @Test fun `borders side order is top-left-bottom-right-insideH-insideV`() {
        val xml = render(TableProperties(borders = TableBorders.DEFAULTS))
        val order = listOf("<w:top ", "<w:left ", "<w:bottom ", "<w:right ", "<w:insideH ", "<w:insideV ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe; got: $xml")
            prev = idx
        }
    }

    @Test fun `borders fills omitted sides with upstream-default`() {
        // Pass only one side; verify the other five fall through to default.
        val xml = render(TableProperties(
            borders = TableBorders(top = io.docxkt.model.border.BorderSide(color = "FF0000")),
        ))
        assertTrue("""w:color="FF0000"""" in xml)
        // Other sides emitted with auto color.
        assertTrue(xml.indexOf("""w:color="auto"""") > 0)
    }

    // --- shading --------------------------------------------------------

    @Test fun `shading clear emits w shd val clear`() {
        val xml = render(TableProperties(
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "DDDDDD"),
        ))
        assertTrue("<w:shd " in xml)
        assertTrue("""w:val="clear"""" in xml)
    }

    // --- cellMargins (tblCellMar) ---------------------------------------

    @Test fun `cellMargins all-null is suppressed even when wrapper emits`() {
        // CellMargins(empty) doesn't add a tblCellMar block.
        val xml = render(TableProperties(
            width = TableWidth.dxa(100),
            cellMargins = CellMargins(),
        ))
        assertFalse("<w:tblCellMar" in xml)
    }

    @Test fun `cellMargins one side emits tblCellMar with that side`() {
        val xml = render(TableProperties(cellMargins = CellMargins(top = 100)))
        assertTrue("<w:tblCellMar>" in xml)
        assertTrue("""<w:top w:type="dxa" w:w="100"/>""" in xml)
    }

    @Test fun `cellMargins all four sides emit in top-left-bottom-right order`() {
        val xml = render(TableProperties(cellMargins = CellMargins(top = 1, left = 2, bottom = 3, right = 4)))
        val order = listOf("""w:w="1"""", """w:w="2"""", """w:w="3"""", """w:w="4"""")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "expected $probe to appear in order; got $xml")
            prev = idx
        }
    }

    // --- canonical order ------------------------------------------------

    @Test fun `tblPr child order is tblW-tblInd-tblBorders-shd-tblLayout-tblCellMar`() {
        val xml = render(TableProperties(
            width = TableWidth.dxa(100),
            indent = TableWidth.dxa(50),
            borders = TableBorders.DEFAULTS,
            shading = Shading(pattern = ShadingPattern.CLEAR, fill = "EEEEEE"),
            layout = TableLayout.FIXED,
            cellMargins = CellMargins(top = 10),
        ))
        val order = listOf("<w:tblW ", "<w:tblInd ", "<w:tblBorders>", "<w:shd ", "<w:tblLayout ", "<w:tblCellMar>")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe in $xml")
            prev = idx
        }
    }
}

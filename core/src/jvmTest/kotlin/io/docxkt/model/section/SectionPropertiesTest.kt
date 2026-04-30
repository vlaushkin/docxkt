// No upstream analogue — SectionProperties matrix.
package io.docxkt.model.section

import io.docxkt.model.border.BorderSide
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class SectionPropertiesTest {

    private fun render(p: SectionProperties): String =
        StringBuilder().apply { p.appendXml(this) }.toString()

    // --- always-present children ----------------------------------------

    @Test fun `default section emits pgSz pgMar pgNumType docGrid`() {
        val xml = render(SectionProperties.default())
        assertTrue("<w:pgSz " in xml)
        assertTrue("<w:pgMar " in xml)
        assertTrue("<w:pgNumType/>" in xml)
        assertTrue("<w:docGrid " in xml)
    }

    @Test fun `default pgSz is A4 portrait 11906x16838`() {
        val xml = render(SectionProperties.default())
        assertTrue("""w:w="11906"""" in xml)
        assertTrue("""w:h="16838"""" in xml)
        assertTrue("""w:orient="portrait"""" in xml)
    }

    @Test fun `landscape pgSz swaps dimensions and orient`() {
        val xml = render(SectionProperties(pageSize = PageSize.a4(PageOrientation.LANDSCAPE)))
        assertTrue("""w:w="16838"""" in xml)
        assertTrue("""w:h="11906"""" in xml)
        assertTrue("""w:orient="landscape"""" in xml)
    }

    @Test fun `default pgMar uses 1440 edges and 708 header-footer`() {
        val xml = render(SectionProperties.default())
        assertTrue("""w:top="1440"""" in xml)
        assertTrue("""w:right="1440"""" in xml)
        assertTrue("""w:bottom="1440"""" in xml)
        assertTrue("""w:left="1440"""" in xml)
        assertTrue("""w:header="708"""" in xml)
        assertTrue("""w:footer="708"""" in xml)
        assertTrue("""w:gutter="0"""" in xml)
    }

    @Test fun `custom margins propagate verbatim`() {
        val xml = render(SectionProperties(
            pageMargins = PageMargins(top = 100, right = 200, bottom = 300, left = 400, header = 50, footer = 60, gutter = 70),
        ))
        assertTrue("""w:top="100"""" in xml)
        assertTrue("""w:right="200"""" in xml)
        assertTrue("""w:bottom="300"""" in xml)
        assertTrue("""w:left="400"""" in xml)
        assertTrue("""w:header="50"""" in xml)
        assertTrue("""w:footer="60"""" in xml)
        assertTrue("""w:gutter="70"""" in xml)
    }

    @Test fun `pgMar attribute order top-right-bottom-left-header-footer-gutter`() {
        val xml = render(SectionProperties.default())
        val order = listOf("w:top=", "w:right=", "w:bottom=", "w:left=", "w:header=", "w:footer=", "w:gutter=")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe")
            prev = idx
        }
    }

    @Test fun `docGrid linePitch is 360 by default`() {
        val xml = render(SectionProperties.default())
        assertTrue("""<w:docGrid w:linePitch="360"/>""" in xml)
    }

    @Test fun `wrapper boundaries`() {
        val xml = render(SectionProperties.default())
        assertTrue(xml.startsWith("<w:sectPr>"))
        assertTrue(xml.endsWith("</w:sectPr>"))
    }

    // --- header / footer references -------------------------------------

    @Test fun `default header reference emits before pgSz`() {
        val xml = render(SectionProperties(
            headerRefs = listOf(HeaderFooterRef(HeaderFooterReferenceType.DEFAULT, "rId7")),
        ))
        assertTrue("""<w:headerReference w:type="default" r:id="rId7"/>""" in xml)
        assertTrue(xml.indexOf("<w:headerReference ") < xml.indexOf("<w:pgSz "))
    }

    @Test fun `header references emit in caller-supplied order`() {
        val xml = render(SectionProperties(
            headerRefs = listOf(
                HeaderFooterRef(HeaderFooterReferenceType.DEFAULT, "rId1"),
                HeaderFooterRef(HeaderFooterReferenceType.FIRST, "rId2"),
                HeaderFooterRef(HeaderFooterReferenceType.EVEN, "rId3"),
            ),
        ))
        val d = xml.indexOf("""w:type="default" r:id="rId1"""")
        val f = xml.indexOf("""w:type="first" r:id="rId2"""")
        val e = xml.indexOf("""w:type="even" r:id="rId3"""")
        assertTrue(d in 0 until f, "expected default before first")
        assertTrue(f in 0 until e, "expected first before even")
    }

    @Test fun `footer references emit after headers`() {
        val xml = render(SectionProperties(
            headerRefs = listOf(HeaderFooterRef(HeaderFooterReferenceType.DEFAULT, "rId1")),
            footerRefs = listOf(
                HeaderFooterRef(HeaderFooterReferenceType.DEFAULT, "rId2"),
                HeaderFooterRef(HeaderFooterReferenceType.FIRST, "rId3"),
                HeaderFooterRef(HeaderFooterReferenceType.EVEN, "rId4"),
            ),
        ))
        assertTrue(xml.indexOf("<w:headerReference ") < xml.indexOf("<w:footerReference "))
    }

    // --- titlePg --------------------------------------------------------

    @Test fun `titlePage true emits self-closing w titlePg`() {
        val xml = render(SectionProperties(titlePage = true))
        assertTrue("<w:titlePg/>" in xml)
    }

    @Test fun `titlePage default false emits no titlePg`() {
        val xml = render(SectionProperties.default())
        assertFalse("<w:titlePg" in xml)
    }

    @Test fun `titlePg sits between pgNumType and docGrid`() {
        val xml = render(SectionProperties(titlePage = true))
        assertTrue(xml.indexOf("<w:pgNumType/>") < xml.indexOf("<w:titlePg/>"))
        assertTrue(xml.indexOf("<w:titlePg/>") < xml.indexOf("<w:docGrid "))
    }

    // --- pageBorders ----------------------------------------------------

    @Test fun `pageBorders null emits no pgBorders`() {
        assertFalse("<w:pgBorders" in render(SectionProperties.default()))
    }

    @Test fun `pageBorders set emits between pgMar and lnNumType-pgNumType`() {
        val xml = render(SectionProperties(pageBorders = PageBorders(top = BorderSide())))
        assertTrue("<w:pgBorders" in xml)
        assertTrue(xml.indexOf("<w:pgMar ") < xml.indexOf("<w:pgBorders"))
        assertTrue(xml.indexOf("<w:pgBorders") < xml.indexOf("<w:pgNumType/>"))
    }

    // --- lineNumbering --------------------------------------------------

    @Test fun `lineNumbering null emits no lnNumType`() {
        assertFalse("<w:lnNumType" in render(SectionProperties.default()))
    }

    @Test fun `lineNumbering between pgBorders and pgNumType`() {
        val xml = render(SectionProperties(
            pageBorders = PageBorders(top = BorderSide()),
            lineNumbering = LineNumbering(countBy = 1, restart = LineNumberRestart.CONTINUOUS),
        ))
        assertTrue(xml.indexOf("<w:pgBorders") < xml.indexOf("<w:lnNumType "))
        assertTrue(xml.indexOf("<w:lnNumType ") < xml.indexOf("<w:pgNumType/>"))
    }

    // --- columns --------------------------------------------------------

    @Test fun `columns null emits no cols`() {
        assertFalse("<w:cols" in render(SectionProperties.default()))
    }

    @Test fun `columns between pgNumType and titlePg`() {
        val xml = render(SectionProperties(columns = Columns(count = 2), titlePage = true))
        assertTrue(xml.indexOf("<w:pgNumType/>") < xml.indexOf("<w:cols "))
        assertTrue(xml.indexOf("<w:cols ") < xml.indexOf("<w:titlePg/>"))
    }
}

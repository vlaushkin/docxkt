// No upstream analogue — Columns matrix.
package io.docxkt.model.section

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ColumnsTest {

    private fun render(c: Columns): String =
        StringBuilder().apply { c.appendXml(this) }.toString()

    @Test fun `count=1 self-closes with just w num`() {
        val xml = render(Columns(count = 1))
        assertTrue("""<w:cols w:num="1"/>""" in xml)
    }

    @Test fun `count + space emits both attrs in order space-num`() {
        // BuilderElement push order: space → num → sep → equalWidth.
        val xml = render(Columns(count = 2, spaceTwips = 720))
        assertTrue("""<w:cols w:space="720" w:num="2"/>""" in xml)
    }

    @Test fun `equalWidth true emits literal "true"`() {
        // Boolean attrs use literal "true"/"false", not OOXML 1/0.
        val xml = render(Columns(count = 2, equalWidth = true))
        assertTrue("""w:equalWidth="true"""" in xml)
    }

    @Test fun `equalWidth false emits literal "false"`() {
        val xml = render(Columns(count = 2, equalWidth = false, individual = listOf(Column(100), Column(200))))
        assertTrue("""w:equalWidth="false"""" in xml)
    }

    @Test fun `separator true emits w sep true`() {
        val xml = render(Columns(count = 2, separator = true))
        assertTrue("""w:sep="true"""" in xml)
    }

    @Test fun `separator false emits w sep false`() {
        val xml = render(Columns(count = 2, separator = false))
        assertTrue("""w:sep="false"""" in xml)
    }

    @Test fun `equalWidth=true self-closes even when individual is non-empty`() {
        // When equalWidth=true, children are not emitted (matches upstream's
        // `!equalWidth && children` condition).
        val xml = render(Columns(count = 2, equalWidth = true, individual = listOf(Column(100), Column(200))))
        assertTrue(xml.endsWith("/>"))
        assertFalse("<w:col " in xml)
    }

    @Test fun `equalWidth=false with individual emits children`() {
        val xml = render(Columns(count = 3, equalWidth = false, individual = listOf(
            Column(widthTwips = 1000, spaceTwips = 100),
            Column(widthTwips = 2000, spaceTwips = 200),
            Column(widthTwips = 3000),
        )))
        assertTrue(xml.startsWith("<w:cols "))
        assertTrue(xml.endsWith("</w:cols>"))
        assertTrue("""<w:col w:w="1000" w:space="100"/>""" in xml)
        assertTrue("""<w:col w:w="2000" w:space="200"/>""" in xml)
        // Last col has no space attr.
        assertTrue("""<w:col w:w="3000"/>""" in xml)
    }

    @Test fun `equalWidth=false but empty individual list still self-closes`() {
        val xml = render(Columns(count = 2, equalWidth = false))
        assertTrue(xml.endsWith("/>"))
    }

    @Test fun `attribute order is space-num-sep-equalWidth`() {
        val xml = render(Columns(count = 3, spaceTwips = 100, separator = true, equalWidth = true))
        val sp = xml.indexOf("w:space=")
        val nu = xml.indexOf("w:num=")
        val se = xml.indexOf("w:sep=")
        val eq = xml.indexOf("w:equalWidth=")
        assertTrue(sp in 0 until nu, "expected space before num")
        assertTrue(nu in 0 until se, "expected num before sep")
        assertTrue(se in 0 until eq, "expected sep before equalWidth")
    }

    @Test fun `Column without space attr is self-closing without space attribute`() {
        val xml = render(Columns(count = 1, equalWidth = false, individual = listOf(Column(2000))))
        assertTrue("""<w:col w:w="2000"/>""" in xml)
        assertFalse("""w:space=""" in xml)
    }

    @Test fun `Column data class equality`() {
        assertEquals(Column(100), Column(100))
        assertEquals(Column(100, 50), Column(100, 50))
    }
}

internal class LineNumberingTest {

    private fun render(l: LineNumbering): String =
        StringBuilder().apply { l.appendXml(this) }.toString()

    @Test fun `all-null emits self-closing w lnNumType with no attrs`() {
        // We don't suppress LineNumbering — it's nullable at the
        // SectionProperties level. When constructed, even all-null
        // emits the bare element.
        val xml = render(LineNumbering())
        assertEquals("<w:lnNumType/>", xml)
    }

    @Test fun `countBy emits w countBy attr`() {
        assertTrue("""w:countBy="5"""" in render(LineNumbering(countBy = 5)))
    }

    @Test fun `start emits w start attr`() {
        assertTrue("""w:start="3"""" in render(LineNumbering(start = 3)))
    }

    @Test fun `distance emits w distance attr`() {
        assertTrue("""w:distance="720"""" in render(LineNumbering(distance = 720)))
    }

    @Test fun `restart CONTINUOUS emits val continuous`() {
        assertTrue("""w:restart="continuous"""" in render(LineNumbering(restart = LineNumberRestart.CONTINUOUS)))
    }

    @Test fun `restart NEW_PAGE emits val newPage`() {
        assertTrue("""w:restart="newPage"""" in render(LineNumbering(restart = LineNumberRestart.NEW_PAGE)))
    }

    @Test fun `restart NEW_SECTION emits val newSection`() {
        assertTrue("""w:restart="newSection"""" in render(LineNumbering(restart = LineNumberRestart.NEW_SECTION)))
    }

    @Test fun `attribute order countBy-start-restart-distance`() {
        val xml = render(LineNumbering(
            countBy = 1, start = 1, distance = 720, restart = LineNumberRestart.CONTINUOUS,
        ))
        val cb = xml.indexOf("w:countBy=")
        val st = xml.indexOf("w:start=")
        val re = xml.indexOf("w:restart=")
        val di = xml.indexOf("w:distance=")
        assertTrue(cb in 0 until st)
        assertTrue(st in 0 until re)
        assertTrue(re in 0 until di)
    }
}

internal class PageBordersTest {

    private fun render(b: PageBorders): String =
        StringBuilder().apply { b.appendXml(this) }.toString()

    @Test fun `empty PageBorders emits open-close pair (no IgnoreIfEmpty)`() {
        // PageBorders is a plain XmlComponent — it always emits.
        // SectionProperties suppresses by null-checking the field.
        assertEquals("<w:pgBorders></w:pgBorders>", render(PageBorders()))
    }

    @Test fun `display attribute emits`() {
        assertTrue("""w:display="allPages"""" in render(PageBorders(display = PageBorderDisplay.ALL_PAGES)))
    }

    @Test fun `offsetFrom PAGE emits val page`() {
        assertTrue("""w:offsetFrom="page"""" in render(PageBorders(offsetFrom = PageBorderOffsetFrom.PAGE)))
    }

    @Test fun `offsetFrom TEXT emits val text`() {
        assertTrue("""w:offsetFrom="text"""" in render(PageBorders(offsetFrom = PageBorderOffsetFrom.TEXT)))
    }

    @Test fun `zOrder FRONT emits val front`() {
        assertTrue("""w:zOrder="front"""" in render(PageBorders(zOrder = PageBorderZOrder.FRONT)))
    }

    @Test fun `zOrder BACK emits val back`() {
        assertTrue("""w:zOrder="back"""" in render(PageBorders(zOrder = PageBorderZOrder.BACK)))
    }

    @Test fun `attribute order is display-offsetFrom-zOrder`() {
        val xml = render(PageBorders(
            display = PageBorderDisplay.ALL_PAGES,
            offsetFrom = PageBorderOffsetFrom.PAGE,
            zOrder = PageBorderZOrder.FRONT,
        ))
        val d = xml.indexOf("w:display=")
        val o = xml.indexOf("w:offsetFrom=")
        val z = xml.indexOf("w:zOrder=")
        assertTrue(d in 0 until o)
        assertTrue(o in 0 until z)
    }

    @Test fun `four sides emit in upstream order top-left-bottom-right`() {
        val xml = render(PageBorders(
            top = io.docxkt.model.border.BorderSide(),
            left = io.docxkt.model.border.BorderSide(),
            bottom = io.docxkt.model.border.BorderSide(),
            right = io.docxkt.model.border.BorderSide(),
        ))
        val order = listOf("<w:top ", "<w:left ", "<w:bottom ", "<w:right ")
        var prev = -1
        for (probe in order) {
            val idx = xml.indexOf(probe)
            assertTrue(idx > prev, "order broken at $probe")
            prev = idx
        }
    }
}

internal class PageMarginsTest {

    @Test fun `PageMargins defaults`() {
        val m = PageMargins()
        assertEquals(1440, m.top)
        assertEquals(1440, m.right)
        assertEquals(1440, m.bottom)
        assertEquals(1440, m.left)
        assertEquals(708, m.header)
        assertEquals(708, m.footer)
        assertEquals(0, m.gutter)
    }

    @Test fun `PageMargins overrides propagate`() {
        val m = PageMargins(top = 100, right = 200, bottom = 300, left = 400, header = 50, footer = 60, gutter = 70)
        assertEquals(100, m.top)
        assertEquals(200, m.right)
        assertEquals(300, m.bottom)
        assertEquals(400, m.left)
        assertEquals(50, m.header)
        assertEquals(60, m.footer)
        assertEquals(70, m.gutter)
    }
}

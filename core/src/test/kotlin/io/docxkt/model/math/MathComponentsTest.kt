// No upstream analogue — math-component matrix.
package io.docxkt.model.math

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MathRunTest {
    @Test fun `MathRun emits m r m t with text`() {
        val out = StringBuilder()
        (MathRun("x") as io.docxkt.xml.XmlComponent).appendXml(out)
        assertEquals("<m:r><m:t>x</m:t></m:r>", out.toString())
    }

    @Test fun `MathRun escapes XML-significant characters in text`() {
        val out = StringBuilder()
        (MathRun("a < b & c") as io.docxkt.xml.XmlComponent).appendXml(out)
        assertEquals("<m:r><m:t>a &lt; b &amp; c</m:t></m:r>", out.toString())
    }

    @Test fun `MathRun does NOT add xml space preserve - upstream m t quirk`() {
        val out = StringBuilder()
        (MathRun(" leading and trailing ") as io.docxkt.xml.XmlComponent).appendXml(out)
        // Upstream's MathText doesn't emit xml:space="preserve" on m:t —
        // unlike <w:t>. We match.
        assertFalse("xml:space" in out.toString())
    }
}

internal class MathRadicalTest {

    private fun render(r: MathRadical): String =
        StringBuilder().apply { (r as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `square root - degree null - emits degHide and empty deg`() {
        val xml = render(MathRadical(children = listOf(MathRun("x"))))
        assertTrue("""<m:rad>""" in xml)
        assertTrue("""<m:radPr><m:degHide m:val="1"/></m:radPr>""" in xml)
        assertTrue("<m:deg/>" in xml)
        assertTrue("<m:e><m:r><m:t>x</m:t></m:r></m:e>" in xml)
    }

    @Test fun `nth root - degree set - emits empty radPr and populated deg`() {
        val xml = render(MathRadical(
            children = listOf(MathRun("x")),
            degree = listOf(MathRun("3")),
        ))
        assertTrue("<m:radPr/>" in xml)
        assertTrue("<m:deg><m:r><m:t>3</m:t></m:r></m:deg>" in xml)
        assertFalse("degHide" in xml)
    }

    @Test fun `wrapper boundaries`() {
        val xml = render(MathRadical(children = listOf(MathRun("x"))))
        assertTrue(xml.startsWith("<m:rad>"))
        assertTrue(xml.endsWith("</m:rad>"))
    }

    @Test fun `child order is radPr then deg then e`() {
        val xml = render(MathRadical(children = listOf(MathRun("a")), degree = listOf(MathRun("b"))))
        val radPr = xml.indexOf("<m:radPr/>")
        val deg = xml.indexOf("<m:deg>")
        val e = xml.indexOf("<m:e>")
        assertTrue(radPr in 0 until deg, "expected radPr before deg")
        assertTrue(deg in 0 until e, "expected deg before e")
    }
}

internal class MathFractionTest {

    private fun render(f: MathFraction): String =
        StringBuilder().apply { (f as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `fraction emits m num then m den - no m fPr`() {
        val xml = render(MathFraction(numerator = listOf(MathRun("a")), denominator = listOf(MathRun("b"))))
        assertEquals(
            "<m:f><m:num><m:r><m:t>a</m:t></m:r></m:num><m:den><m:r><m:t>b</m:t></m:r></m:den></m:f>",
            xml,
        )
    }

    @Test fun `fraction num precedes den`() {
        val xml = render(MathFraction(numerator = listOf(MathRun("a")), denominator = listOf(MathRun("b"))))
        assertTrue(xml.indexOf("<m:num>") < xml.indexOf("<m:den>"))
    }

    @Test fun `empty num and den still emit wrappers`() {
        val xml = render(MathFraction(numerator = emptyList(), denominator = emptyList()))
        assertEquals("<m:f><m:num></m:num><m:den></m:den></m:f>", xml)
    }
}

internal class MathBracketsTest {

    private fun render(b: MathBrackets): String =
        StringBuilder().apply { (b as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `default brackets - no begin no end - emits empty m dPr`() {
        val xml = render(MathBrackets(children = listOf(MathRun("x"))))
        assertTrue("<m:dPr/>" in xml)
        assertFalse("<m:begChr" in xml)
        assertFalse("<m:endChr" in xml)
    }

    @Test fun `square brackets emit begChr and endChr`() {
        val xml = render(MathBrackets(children = listOf(MathRun("x")), begin = "[", end = "]"))
        assertTrue("""<m:begChr m:val="["/>""" in xml)
        assertTrue("""<m:endChr m:val="]"/>""" in xml)
        assertTrue("<m:dPr>" in xml)
        assertTrue("</m:dPr>" in xml)
    }

    @Test fun `curly brackets`() {
        val xml = render(MathBrackets(children = emptyList(), begin = "{", end = "}"))
        assertTrue("""<m:begChr m:val="{"/>""" in xml)
        assertTrue("""<m:endChr m:val="}"/>""" in xml)
    }

    @Test fun `angled brackets U+27E8 U+27E9`() {
        val xml = render(MathBrackets(children = emptyList(), begin = "⟨", end = "⟩"))
        assertTrue("""<m:begChr m:val="⟨"/>""" in xml || """<m:begChr m:val="⟨"/>""" in xml)
    }

    @Test fun `bracket characters are XML-escaped in attribute`() {
        val xml = render(MathBrackets(children = emptyList(), begin = "<", end = ">"))
        assertTrue("""<m:begChr m:val="&lt;"/>""" in xml)
        assertTrue("""<m:endChr m:val="&gt;"/>""" in xml)
    }

    @Test fun `begin only with end null still emits dPr block`() {
        val xml = render(MathBrackets(children = emptyList(), begin = "["))
        assertTrue("""<m:begChr m:val="["/>""" in xml)
        assertFalse("<m:endChr" in xml)
    }

    @Test fun `end only with begin null still emits dPr block`() {
        val xml = render(MathBrackets(children = emptyList(), end = "]"))
        assertTrue("""<m:endChr m:val="]"/>""" in xml)
        assertFalse("<m:begChr" in xml)
    }
}

internal class MathScriptsTest {

    private fun render(c: MathComponent): String =
        StringBuilder().apply { (c as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `superScript emits m sSup with sSupPr e and sup`() {
        val xml = render(MathSuperScript(
            children = listOf(MathRun("x")),
            superScript = listOf(MathRun("2")),
        ))
        assertTrue(xml.startsWith("<m:sSup>"))
        assertTrue("<m:sSupPr/>" in xml)
        assertTrue("<m:e><m:r><m:t>x</m:t></m:r></m:e>" in xml)
        assertTrue("<m:sup><m:r><m:t>2</m:t></m:r></m:sup>" in xml)
    }

    @Test fun `subScript emits m sSub`() {
        val xml = render(MathSubScript(
            children = listOf(MathRun("x")),
            subScript = listOf(MathRun("i")),
        ))
        assertTrue(xml.startsWith("<m:sSub>"))
        assertTrue("<m:sSubPr/>" in xml)
        assertTrue("<m:sub><m:r><m:t>i</m:t></m:r></m:sub>" in xml)
    }

    @Test fun `subSuperScript emits m sSubSup with both sub and sup`() {
        val xml = render(MathSubSuperScript(
            children = listOf(MathRun("x")),
            subScript = listOf(MathRun("i")),
            superScript = listOf(MathRun("2")),
        ))
        assertTrue(xml.startsWith("<m:sSubSup>"))
        assertTrue("<m:sSubSupPr/>" in xml)
        // Upstream order: e → sub → sup.
        val e = xml.indexOf("<m:e>")
        val sub = xml.indexOf("<m:sub>")
        val sup = xml.indexOf("<m:sup>")
        assertTrue(e in 0 until sub)
        assertTrue(sub in 0 until sup)
    }

    @Test fun `preSubSuperScript emits m sPre`() {
        val xml = render(MathPreSubSuperScript(
            children = listOf(MathRun("T")),
            subScript = listOf(MathRun("i")),
            superScript = listOf(MathRun("j")),
        ))
        assertTrue(xml.startsWith("<m:sPre>"))
        assertTrue("<m:sPrePr/>" in xml)
    }
}

internal class MathNAryTest {

    private fun render(n: MathNAry): String =
        StringBuilder().apply { (n as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `sum with both limits emits chr sum, undOvr limLoc, sub and sup`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            subScript = listOf(MathRun("i=1")),
            superScript = listOf(MathRun("n")),
            accentChar = MathNAry.SUM_CHAR,
            limitLocation = MathLimitLocation.UNDER_OVER,
        ))
        assertTrue("""<m:chr m:val="∑"/>""" in xml)
        assertTrue("""<m:limLoc m:val="undOvr"/>""" in xml)
        assertTrue("<m:sub>" in xml)
        assertTrue("<m:sup>" in xml)
        assertTrue("<m:e>" in xml)
    }

    @Test fun `integral emits no chr - integral-style accentChar null`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("f(x)")),
            subScript = listOf(MathRun("0")),
            superScript = listOf(MathRun("1")),
            accentChar = null,
            limitLocation = MathLimitLocation.SUB_SUP,
        ))
        assertFalse("<m:chr " in xml)
        assertTrue("""<m:limLoc m:val="subSup"/>""" in xml)
    }

    @Test fun `product emits chr 8719`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            accentChar = MathNAry.PRODUCT_CHAR,
        ))
        assertTrue("""<m:chr m:val="∏"/>""" in xml)
    }

    @Test fun `missing super emits supHide`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            subScript = listOf(MathRun("i=1")),
            superScript = null,
            accentChar = MathNAry.SUM_CHAR,
        ))
        assertTrue("""<m:supHide m:val="1"/>""" in xml)
        assertFalse("<m:supHide" !in xml || "<m:sup>" in xml)
    }

    @Test fun `missing sub emits subHide`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            subScript = null,
            superScript = listOf(MathRun("n")),
            accentChar = MathNAry.SUM_CHAR,
        ))
        assertTrue("""<m:subHide m:val="1"/>""" in xml)
    }

    @Test fun `naryPr child order is chr-limLoc-supHide-subHide`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            subScript = null,
            superScript = null,
            accentChar = MathNAry.SUM_CHAR,
        ))
        val chr = xml.indexOf("<m:chr ")
        val limLoc = xml.indexOf("<m:limLoc ")
        val supHide = xml.indexOf("<m:supHide ")
        val subHide = xml.indexOf("<m:subHide ")
        assertTrue(chr in 0 until limLoc)
        assertTrue(limLoc in 0 until supHide)
        assertTrue(supHide in 0 until subHide)
    }

    @Test fun `body order is naryPr-sub-sup-e`() {
        val xml = render(MathNAry(
            children = listOf(MathRun("x")),
            subScript = listOf(MathRun("a")),
            superScript = listOf(MathRun("b")),
            accentChar = MathNAry.SUM_CHAR,
        ))
        val pr = xml.indexOf("<m:naryPr>")
        val sub = xml.indexOf("<m:sub>")
        val sup = xml.indexOf("<m:sup>")
        val e = xml.indexOf("<m:e>")
        assertTrue(pr in 0 until sub)
        assertTrue(sub in 0 until sup)
        assertTrue(sup in 0 until e)
    }

    @Test fun `SUM_CHAR is unicode N-ARY SUMMATION U+2211`() {
        assertEquals("∑", MathNAry.SUM_CHAR)
    }

    @Test fun `PRODUCT_CHAR is unicode N-ARY PRODUCT U+220F`() {
        assertEquals("∏", MathNAry.PRODUCT_CHAR)
    }
}

internal class MathFunctionTest {

    private fun render(f: MathFunction): String =
        StringBuilder().apply { (f as io.docxkt.xml.XmlComponent).appendXml(this) }.toString()

    @Test fun `function emits funcPr fName e in order`() {
        val xml = render(MathFunction(
            name = listOf(MathRun("sin")),
            children = listOf(MathRun("x")),
        ))
        val fp = xml.indexOf("<m:funcPr/>")
        val fn = xml.indexOf("<m:fName>")
        val e = xml.indexOf("<m:e>")
        assertTrue(fp in 0 until fn)
        assertTrue(fn in 0 until e)
    }

    @Test fun `function name is plain MathRun list`() {
        val xml = render(MathFunction(
            name = listOf(MathRun("log")),
            children = listOf(MathRun("y")),
        ))
        assertTrue("<m:fName><m:r><m:t>log</m:t></m:r></m:fName>" in xml)
    }
}

internal class OMathTest {

    @Test fun `OMath wraps children in m oMath element`() {
        val out = StringBuilder()
        (OMath(children = listOf(MathRun("x"))) as io.docxkt.xml.XmlComponent).appendXml(out)
        val xml = out.toString()
        assertTrue(xml.startsWith("<m:oMath>"))
        assertTrue(xml.endsWith("</m:oMath>"))
        assertTrue("<m:r><m:t>x</m:t></m:r>" in xml)
    }
}

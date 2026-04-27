// No upstream analogue — Kotlin test of OnOff semantics on
// RunProperties' boolean fields. Mirrors the implicit coverage from
// fixture-backed tests but exhausts the field × state matrix.
package io.docxkt.model.paragraph.run

import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class RunPropertiesBooleanMatrixTest {

    /**
     * For every plain-OnOff field, three cases:
     *  - null → element absent
     *  - true → self-closing tag
     *  - false → tag with `w:val="false"`
     *
     * The fields with quirks (vanish truthy-only, super/sub which
     * emit `<w:vertAlign>`, smallCaps/allCaps mutual exclusion) get
     * their own targeted tests below.
     */
    @TestFactory
    fun `OnOff matrix`(): List<DynamicTest> {
        data class Case(
            val name: String,
            val element: String,
            val mirror: String? = null,
            val factory: (Boolean?) -> RunProperties,
        )
        val cases = listOf(
            Case("bold", "w:b", "w:bCs") { RunProperties(bold = it) },
            Case("italics", "w:i", "w:iCs") { RunProperties(italics = it) },
            Case("strike", "w:strike") { RunProperties(strike = it) },
            Case("doubleStrike", "w:dstrike") { RunProperties(doubleStrike = it) },
            Case("emboss", "w:emboss") { RunProperties(emboss = it) },
            Case("imprint", "w:imprint") { RunProperties(imprint = it) },
            Case("noProof", "w:noProof") { RunProperties(noProof = it) },
            Case("snapToGrid", "w:snapToGrid") { RunProperties(snapToGrid = it) },
            Case("rightToLeft", "w:rtl") { RunProperties(rightToLeft = it) },
        )
        return cases.flatMap { case ->
            listOf(
                dynamicTest("${case.name} null does not emit ${case.element}") {
                    val xml = render(case.factory(null))
                    assertFalse("<${case.element}" in xml, "expected no ${case.element}; got: $xml")
                },
                dynamicTest("${case.name} true emits self-closing ${case.element}") {
                    val xml = render(case.factory(true))
                    assertTrue("<${case.element}/>" in xml, "expected <${case.element}/>; got: $xml")
                },
                dynamicTest("${case.name} false emits ${case.element} with val false") {
                    val xml = render(case.factory(false))
                    assertTrue("""<${case.element} w:val="false"/>""" in xml, "expected ${case.element} val=false; got: $xml")
                },
            ) + (case.mirror?.let { mirror ->
                listOf(
                    dynamicTest("${case.name} true also emits $mirror mirror") {
                        val xml = render(case.factory(true))
                        assertTrue("<$mirror/>" in xml, "expected <$mirror/>; got: $xml")
                    },
                    dynamicTest("${case.name} false also emits $mirror mirror with val false") {
                        val xml = render(case.factory(false))
                        assertTrue("""<$mirror w:val="false"/>""" in xml, "expected $mirror val=false; got: $xml")
                    },
                )
            } ?: emptyList())
        }
    }

    // --- smallCaps / allCaps mutual exclusion ---------------------------

    @Test fun `smallCaps null and allCaps null emit nothing`() {
        val xml = render(RunProperties())
        assertFalse("<w:smallCaps" in xml)
        assertFalse("<w:caps" in xml)
    }

    @Test fun `smallCaps true emits w smallCaps`() {
        val xml = render(RunProperties(smallCaps = true))
        assertTrue("<w:smallCaps/>" in xml)
    }

    @Test fun `smallCaps false emits w smallCaps val false`() {
        val xml = render(RunProperties(smallCaps = false))
        assertTrue("""<w:smallCaps w:val="false"/>""" in xml)
    }

    @Test fun `allCaps true emits w caps when smallCaps unset`() {
        val xml = render(RunProperties(allCaps = true))
        assertTrue("<w:caps/>" in xml)
        assertFalse("<w:smallCaps" in xml)
    }

    @Test fun `allCaps false emits w caps val false when smallCaps unset`() {
        val xml = render(RunProperties(allCaps = false))
        assertTrue("""<w:caps w:val="false"/>""" in xml)
    }

    @Test fun `smallCaps wins when both smallCaps and allCaps set`() {
        // Upstream emits smallCaps and skips allCaps if both are set.
        val xml = render(RunProperties(smallCaps = true, allCaps = true))
        assertTrue("<w:smallCaps/>" in xml)
        assertFalse("<w:caps" in xml)
    }

    @Test fun `smallCaps wins even when smallCaps is false`() {
        // The mutex is on presence, not truthiness.
        val xml = render(RunProperties(smallCaps = false, allCaps = true))
        assertTrue("""<w:smallCaps w:val="false"/>""" in xml)
        assertFalse("<w:caps" in xml)
    }

    // --- vanish truthy-only quirk ---------------------------------------

    @Test fun `vanish null emits nothing`() {
        assertFalse("<w:vanish" in render(RunProperties(vanish = null)))
    }

    @Test fun `vanish false emits nothing - upstream truthy-only quirk`() {
        // Upstream emits <w:vanish/> only when the value is truthy;
        // false should NOT produce <w:vanish w:val="false"/>.
        assertFalse("<w:vanish" in render(RunProperties(vanish = false)))
    }

    @Test fun `vanish true emits self-closing w vanish`() {
        assertTrue("<w:vanish/>" in render(RunProperties(vanish = true)))
    }

    // --- super / subScript emit w vertAlign -----------------------------

    @Test fun `superScript null emits no vertAlign`() {
        assertFalse("<w:vertAlign" in render(RunProperties(superScript = null)))
    }

    @Test fun `superScript false emits no vertAlign - truthy-only`() {
        // The implementation only emits vertAlign on true.
        assertFalse("<w:vertAlign" in render(RunProperties(superScript = false)))
    }

    @Test fun `superScript true emits w vertAlign val superscript`() {
        val xml = render(RunProperties(superScript = true))
        assertTrue("""<w:vertAlign w:val="superscript"/>""" in xml)
    }

    @Test fun `subScript true emits w vertAlign val subscript`() {
        val xml = render(RunProperties(subScript = true))
        assertTrue("""<w:vertAlign w:val="subscript"/>""" in xml)
    }

    @Test fun `subScript and superScript both true emits both vertAlign elements`() {
        // The implementation emits both — sub before super. Word's
        // last-wins semantics make that the de-facto super-script.
        val xml = render(RunProperties(subScript = true, superScript = true))
        assertTrue("""<w:vertAlign w:val="subscript"/>""" in xml)
        assertTrue("""<w:vertAlign w:val="superscript"/>""" in xml)
        assertTrue(xml.indexOf("subscript") < xml.indexOf("superscript"))
    }

    // --- helper ---------------------------------------------------------

    private fun render(rPr: RunProperties): String {
        val out = StringBuilder()
        rPr.appendXml(out)
        return out.toString()
    }
}

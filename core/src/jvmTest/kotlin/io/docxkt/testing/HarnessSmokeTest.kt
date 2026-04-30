// Smoke-tests the test harness helpers (zipEntriesAsText, assertXmlEquals,
// FixtureLoader) without relying on fixtures on disk or the real Document.
package io.docxkt.testing

import io.docxkt.pack.DocxPackager
import io.docxkt.pack.toByteArray
import org.junit.jupiter.api.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

internal class HarnessSmokeTest {

    @Test
    fun `zipEntriesAsText round-trips text parts through a docx ZIP`() {
        val parts = listOf(
            DocxPackager.Entry("word/document.xml", "<w:document/>".toByteArray()),
            DocxPackager.Entry("[Content_Types].xml", "<Types/>".toByteArray()),
            DocxPackager.Entry("_rels/.rels", "<Relationships/>".toByteArray()),
        )
        val zipped = DocxPackager.toByteArray(parts)
        val read = zipEntriesAsText(zipped)

        assertEquals("<w:document/>", read["word/document.xml"])
        assertEquals("<Types/>", read["[Content_Types].xml"])
        assertEquals("<Relationships/>", read["_rels/.rels"])
    }

    @Test
    fun `assertXmlEquals accepts semantically equal XML with differing whitespace`() {
        val a = """<root><a attr="1"/><b/></root>"""
        val b = """
            <root>
                <a attr="1"/>
                <b/>
            </root>
        """.trimIndent()
        // Throws on mismatch; returns normally otherwise.
        assertXmlEquals(a, b, context = "smoke")
    }

    @Test
    fun `assertXmlEquals fails with diagnostic when XML differs`() {
        val expected = """<root><a attr="1"/></root>"""
        val actual = """<root><a attr="2"/></root>"""
        val error = assertFailsWith<AssertionError> {
            assertXmlEquals(actualXml = actual, expectedXml = expected, context = "smoke-context")
        }
        assertContains(error.message!!, "smoke-context")
        assertContains(error.message!!, "[EXPECTED]")
        assertContains(error.message!!, "[ACTUAL]")
    }

    @Test
    fun `FixtureLoader raises a clear error when fixture is absent`() {
        val error = assertFailsWith<IllegalStateException> {
            FixtureLoader.loadPart("definitely-not-a-fixture", "word/document.xml")
        }
        assertContains(error.message!!, "definitely-not-a-fixture")
    }
}

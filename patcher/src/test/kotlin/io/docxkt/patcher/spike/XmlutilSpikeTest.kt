package io.docxkt.patcher.spike

import nl.adaptivity.xmlutil.newReader
import nl.adaptivity.xmlutil.newWriter
import nl.adaptivity.xmlutil.writeCurrent
import nl.adaptivity.xmlutil.xmlStreaming
import org.junit.jupiter.api.Test
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors
import java.io.StringReader
import java.io.StringWriter
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Verifies that pdvrieze/xmlutil preserves the byte-level XML features the
 * patcher's round-trip pipeline depends on. The fixture
 * `demo-76-compatibility/word/document.xml` carries 30+ namespace
 * declarations on `<w:document>` in a specific source order, an
 * `mc:Ignorable="w14 w15 wp14"` attribute (markup-compatibility flag list),
 * and `xml:space="preserve"` on `<w:t>`. Word and LibreOffice depend on
 * these surviving any read-modify-write cycle.
 */
internal class XmlutilSpikeTest {

    private val fixturePath = "/fixtures/patcher-combined-all-types/input/word/document.xml"

    private fun loadFixture(): String =
        javaClass.getResourceAsStream(fixturePath)!!.readBytes().decodeToString()

    private fun roundTrip(input: String): String {
        val reader = xmlStreaming.newReader(StringReader(input))
        val out = StringWriter()
        xmlStreaming.newWriter(out).use { writer ->
            while (reader.hasNext()) {
                reader.next()
                reader.writeCurrent(writer)
            }
        }
        return out.toString()
    }

    @Test
    fun `byte-roundtrip is XMLUnit-equivalent under patcher diff configuration`() {
        val input = loadFixture()
        val output = roundTrip(input)

        val diff = DiffBuilder.compare(Input.fromString(input))
            .withTest(Input.fromString(output))
            .ignoreWhitespace()
            .withNodeMatcher(DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
            .checkForIdentical()
            .build()

        assertTrue(
            !diff.hasDifferences(),
            "XMLUnit diff after xmlutil round-trip:\n${diff.differences.joinToString("\n")}"
        )
    }

    @Test
    fun `namespace declaration source order survives round-trip`() {
        val input = loadFixture()
        val output = roundTrip(input)

        val expected = extractNamespacePrefixes(input)
        val actual = extractNamespacePrefixes(output)

        assertEquals(
            expected, actual,
            "xmlutil reordered namespace declarations on root.\n" +
                "expected: $expected\nactual:   $actual"
        )
    }

    @Test
    fun `xml-space preserve attribute survives round-trip`() {
        val input = loadFixture()
        val output = roundTrip(input)

        assertTrue(
            output.contains("xml:space=\"preserve\""),
            "xml:space=\"preserve\" lost during round-trip"
        )
    }

    @Test
    fun `duplicate tokens in mc-Ignorable are not deduplicated`() {
        // mc:Ignorable holds a token list; Word treats `"a a b a"` as a list
        // of four tokens — semantically allowed and we must not collapse.
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<root xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" mc:Ignorable="a a b a"/>"""
        val output = roundTrip(xml)

        assertTrue(
            output.contains("mc:Ignorable=\"a a b a\""),
            "Duplicate-token attribute value was modified. Output:\n$output"
        )
    }

    @Test
    fun `entity references round-trip without semantic loss`() {
        val xml = """<?xml version="1.0" encoding="UTF-8"?>
<r><t>foo &amp; bar &lt; baz</t></r>"""
        val output = roundTrip(xml)

        val diff = DiffBuilder.compare(Input.fromString(xml))
            .withTest(Input.fromString(output))
            .ignoreWhitespace()
            .checkForIdentical()
            .build()

        assertTrue(
            !diff.hasDifferences(),
            "Entity round-trip differs:\n${diff.differences.joinToString("\n")}"
        )
    }

    private fun extractNamespacePrefixes(xml: String): List<String> {
        val rootStart = xml.indexOf('<', xml.indexOf("?>") + 2)
        val rootEnd = xml.indexOf('>', rootStart)
        val rootTag = xml.substring(rootStart, rootEnd + 1)
        val regex = Regex("""xmlns:([\w-]+)=""")
        return regex.findAll(rootTag).map { it.groupValues[1] }.toList()
    }
}

// No upstream analogue — synthetic invariant test for the
// patcher's stateless contract.
package io.docxkt.patcher

import io.docxkt.pack.toByteArray
import io.docxkt.pack.DocxPackager
import org.xmlunit.builder.DiffBuilder
import org.xmlunit.builder.Input
import org.xmlunit.diff.DefaultNodeMatcher
import org.xmlunit.diff.ElementSelectors
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Idempotency / order-independence properties of [PatchDocument.patch].
 *
 * For disjoint marker sets, the document `<w:body>` content
 * produced by `patch(patch(d, m1), m2)` is XML-equal to the body
 * produced by `patch(d, m1 ∪ m2)`. The patcher has no per-call
 * state that would leak between invocations.
 *
 * **Caveat — `mc:Ignorable` is intentionally non-idempotent.** The
 * post-load `ensureDocumentNamespaces` pass mirrors upstream's
 * `from-docx.ts` and unconditionally appends `" w15"` on each call
 * — producing the trailing `w15 w15` duplicate seen in real Word
 * templates. This is upstream-byte-equal behaviour; the test
 * extracts the `<w:body>` only to compare patched content.
 */
internal class PatcherIdempotencyTest {

    @Test
    fun `disjoint text patches commute`() {
        val source = packMinimalDocx(
            "<w:p><w:r><w:t xml:space=\"preserve\">{{a}} and {{b}} together</w:t></w:r></w:p>",
        )

        val viaSequential = PatchDocument.patch(
            PatchDocument.patch(source, mapOf("a" to Patch.Text("ALPHA"))),
            mapOf("b" to Patch.Text("BETA")),
        )

        val viaUnion = PatchDocument.patch(
            source,
            mapOf("a" to Patch.Text("ALPHA"), "b" to Patch.Text("BETA")),
        )

        assertBodyXmlEqual(
            actual = extractDocumentXml(viaSequential),
            expected = extractDocumentXml(viaUnion),
            context = "sequential vs union with disjoint text patches",
        )
    }

    @Test
    fun `repeating same patch on patched output is identity`() {
        val source = packMinimalDocx(
            "<w:p><w:r><w:t xml:space=\"preserve\">{{name}}</w:t></w:r></w:p>",
        )

        val first = PatchDocument.patch(source, mapOf("name" to Patch.Text("Vasily")))
        // Re-patching with the SAME map: the marker is gone, so the
        // patcher should leave the body untouched (even though
        // ensureDocumentNamespaces appends another " w15").
        val second = PatchDocument.patch(first, mapOf("name" to Patch.Text("Vasily")))

        assertBodyXmlEqual(
            actual = extractDocumentXml(second),
            expected = extractDocumentXml(first),
            context = "patch(patch(d, m), m) body ≡ patch(d, m) body for vanished marker",
        )
    }

    @Test
    fun `unmatched key is silently dropped`() {
        val source = packMinimalDocx(
            "<w:p><w:r><w:t xml:space=\"preserve\">no markers here</w:t></w:r></w:p>",
        )
        val out = PatchDocument.patch(source, mapOf("missing" to Patch.Text("X")))
        // No exception, no change to the document body.
        val xml = extractDocumentXml(out)
        assertTrue(xml.contains("no markers here"))
        assertFalse(xml.contains("X"))
    }

    private fun packMinimalDocx(bodyChildren: String): ByteArray {
        val docXml =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body>$bodyChildren</w:body>" +
                "</w:document>"
        val contentTypes =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                "</Types>"
        return DocxPackager.toByteArray(
            listOf(
                DocxPackager.Entry("word/document.xml", docXml.toByteArray(Charsets.UTF_8)),
                DocxPackager.Entry("[Content_Types].xml", contentTypes.toByteArray(Charsets.UTF_8)),
            ),
        )
    }

    private fun extractDocumentXml(docxBytes: ByteArray): String {
        ZipInputStream(ByteArrayInputStream(docxBytes)).use { zin ->
            while (true) {
                val entry = zin.nextEntry ?: break
                if (entry.name == "word/document.xml") {
                    return zin.readAllBytes().toString(Charsets.UTF_8)
                }
            }
        }
        error("word/document.xml not found in patched bytes")
    }

    private fun assertBodyXmlEqual(actual: String, expected: String, context: String) {
        val actualBody = extractBody(actual)
        val expectedBody = extractBody(expected)
        val diff = DiffBuilder.compare(Input.fromString(expectedBody))
            .withTest(Input.fromString(actualBody))
            .ignoreWhitespace()
            .withNodeMatcher(DefaultNodeMatcher(ElementSelectors.byNameAndAllAttributes))
            .checkForIdentical()
            .build()
        if (diff.hasDifferences()) {
            error(
                "$context — body XML differences:\n${diff.differences.joinToString("\n")}\n\n" +
                    "actual body:\n$actualBody\n\nexpected body:\n$expectedBody",
            )
        }
    }

    /**
     * Pull `<w:body>...</w:body>` out of a `<w:document>` so the
     * comparison ignores root-level namespace re-decoration.
     */
    private fun extractBody(documentXml: String): String {
        val regex = Regex("(?s)<w:body[^>]*>.*?</w:body>")
        val match = regex.find(documentXml)
            ?: error("could not locate <w:body> in document XML")
        return "<root xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
            match.value +
            "</root>"
    }
}

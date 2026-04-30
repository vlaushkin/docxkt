// No upstream analogue — synthetic negative-path coverage for the
// patcher's user-supplied ZIP intake.
package io.docxkt.patcher

import io.docxkt.pack.toByteArray
import io.docxkt.model.drawing.ImageFormat
import io.docxkt.pack.DocxPackager
import java.util.zip.ZipInputStream
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * Negative-path tests for [PatchDocument.patch].
 *
 * Each scenario constructs a deliberately-malformed input and
 * asserts the patcher fails fast with a recognisable exception
 * instead of silently producing garbage. Mirrors upstream's
 * `from-docx.ts` validation guards.
 */
internal class PatcherMalformedInputTest {

    /**
     * Random bytes that aren't a ZIP archive. JDK ZipInputStream
     * behaviour on non-ZIP magic bytes is "silently produce zero
     * entries"; the patcher inherits that and returns an empty
     * ZIP. Documented contract.
     */
    @Test
    fun `non-ZIP input produces empty re-zip without throwing`() {
        val notZip = "this is not a zip file at all".toByteArray(Charsets.UTF_8)
        val out = PatchDocument.patch(notZip, patches = mapOf("k" to Patch.Text("v")))
        assertTrue(out.isNotEmpty(), "expected an empty re-zipped output, got 0 bytes")
        // Output is a valid (but empty) ZIP.
        ZipInputStream(out.inputStream()).use { zin ->
            assertTrue(zin.nextEntry == null, "expected no entries in re-zipped output")
        }
    }

    /**
     * ZIP entry with malformed XML inside `word/document.xml`. The
     * StAX parser surfaces this as a `XMLStreamException`.
     */
    @Test
    fun `malformed XML in document_xml throws`() {
        val docx = DocxPackager.toByteArray(
            listOf(
                DocxPackager.Entry(
                    path = "word/document.xml",
                    bytes = "<not valid xml at all".toByteArray(Charsets.UTF_8),
                ),
                DocxPackager.Entry(
                    path = "[Content_Types].xml",
                    bytes = MIN_CONTENT_TYPES.toByteArray(Charsets.UTF_8),
                ),
            ),
        )
        assertFailsWith<Exception> {
            PatchDocument.patch(docx, patches = mapOf("k" to Patch.Text("v")))
        }
    }

    /** Empty placeholderDelimiters — would match every char span. */
    @Test
    fun `empty placeholder delimiter is rejected`() {
        val ex = assertFailsWith<IllegalArgumentException> {
            PatchDocument.patch(
                data = packEmptyDocx(),
                patches = mapOf("k" to Patch.Text("v")),
                placeholderDelimiters = "" to "",
            )
        }
        assertTrue(
            ex.message!!.contains("non-empty"),
            "expected message to mention non-empty; got: ${ex.message}",
        )
    }

    /** Empty start-only delimiter is rejected. */
    @Test
    fun `empty start delimiter is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            PatchDocument.patch(
                data = packEmptyDocx(),
                patches = mapOf("k" to Patch.Text("v")),
                placeholderDelimiters = "" to "}}",
            )
        }
    }

    /** Empty end-only delimiter is rejected. */
    @Test
    fun `empty end delimiter is rejected`() {
        assertFailsWith<IllegalArgumentException> {
            PatchDocument.patch(
                data = packEmptyDocx(),
                patches = mapOf("k" to Patch.Text("v")),
                placeholderDelimiters = "{{" to "",
            )
        }
    }

    /**
     * Patch.Image without `[Content_Types].xml` in input — the
     * orchestrator throws via `error("Patch.Image requires [Content_Types].xml in input")`.
     */
    @Test
    fun `Patch_Image without ContentTypes throws`() {
        val onlyDocument = DocxPackager.toByteArray(
            listOf(
                DocxPackager.Entry(
                    path = "word/document.xml",
                    bytes = MIN_DOC_WITH_MARKER.toByteArray(Charsets.UTF_8),
                ),
            ),
        )
        val ex = assertFailsWith<IllegalStateException> {
            PatchDocument.patch(
                data = onlyDocument,
                patches = mapOf(
                    "logo" to Patch.Image(
                        bytes = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47),
                        widthEmus = 100_000,
                        heightEmus = 100_000,
                        format = ImageFormat.PNG,
                    ),
                ),
            )
        }
        assertTrue(
            ex.message!!.contains("Content_Types"),
            "expected message to mention Content_Types; got: ${ex.message}",
        )
    }

    /**
     * Missing word/document.xml — the orchestrator currently
     * silently no-ops (returns input verbatim). Documented behaviour;
     * the test pins it so any future strictening is intentional.
     */
    @Test
    fun `missing document_xml is silent no-op today`() {
        val noDocument = DocxPackager.toByteArray(
            listOf(
                DocxPackager.Entry(
                    path = "[Content_Types].xml",
                    bytes = MIN_CONTENT_TYPES.toByteArray(Charsets.UTF_8),
                ),
            ),
        )
        val out = PatchDocument.patch(
            data = noDocument,
            patches = mapOf("k" to Patch.Text("v")),
        )
        assertTrue(out.isNotEmpty(), "patcher should still return a re-zipped output")
    }

    private fun packEmptyDocx(): ByteArray = DocxPackager.toByteArray(
        listOf(
            DocxPackager.Entry(
                path = "word/document.xml",
                bytes = MIN_DOC_WITH_MARKER.toByteArray(Charsets.UTF_8),
            ),
            DocxPackager.Entry(
                path = "[Content_Types].xml",
                bytes = MIN_CONTENT_TYPES.toByteArray(Charsets.UTF_8),
            ),
        ),
    )

    companion object {
        private const val MIN_DOC_WITH_MARKER =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<w:document xmlns:w=\"http://schemas.openxmlformats.org/wordprocessingml/2006/main\">" +
                "<w:body><w:p><w:r><w:t xml:space=\"preserve\">{{logo}} text</w:t></w:r></w:p></w:body>" +
                "</w:document>"
        private const val MIN_CONTENT_TYPES =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">" +
                "<Default Extension=\"xml\" ContentType=\"application/xml\"/>" +
                "</Types>"
    }
}

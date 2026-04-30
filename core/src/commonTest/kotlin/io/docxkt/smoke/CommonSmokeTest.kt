package io.docxkt.smoke

import io.docxkt.api.document
import io.docxkt.api.toByteArray
import io.docxkt.api.writeTo
import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Cross-platform smoke coverage for the public document-build pipeline.
 * The full byte-equal regression battery lives in `jvmTest` and uses
 * XMLUnit; this file only proves that every target's link path produces
 * a runnable .docx envelope, exercising the platform IO codec, ZIP
 * framer, and DSL emission code paths.
 */
class CommonSmokeTest {

    @Test
    fun `empty paragraph produces a non-trivial docx`() {
        val bytes = document { paragraph {} }.toByteArray()
        assertTrue(bytes.size > 200, "Expected > 200 bytes, got ${bytes.size}")
    }

    @Test
    fun `output begins with the ZIP local-file-header signature`() {
        val bytes = document { paragraph { text("hi") } }.toByteArray()
        assertEquals(0x50.toByte(), bytes[0])
        assertEquals(0x4B.toByte(), bytes[1])
        assertEquals(0x03.toByte(), bytes[2])
        assertEquals(0x04.toByte(), bytes[3])
    }

    @Test
    fun `bold inline run serializes`() {
        val bytes = document {
            paragraph {
                text("emphasized") { bold = true }
            }
        }.toByteArray()
        assertTrue(bytes.size > 200)
    }

    @Test
    fun `multi-paragraph document serializes`() {
        val bytes = document {
            paragraph { text("first") }
            paragraph { text("second") }
            paragraph { text("third") }
        }.toByteArray()
        assertTrue(bytes.size > 200)
    }

    @Test
    fun `table with one row and two cells serializes`() {
        val bytes = document {
            table {
                row {
                    cell { paragraph { text("A1") } }
                    cell { paragraph { text("B1") } }
                }
            }
        }.toByteArray()
        assertTrue(bytes.size > 300)
    }

    @Test
    fun `writeTo Sink and toByteArray produce identical bytes`() {
        val doc = document {
            paragraph { text("identity") }
        }
        val viaArray = doc.toByteArray()
        val buf = Buffer()
        doc.writeTo(buf)
        val viaSink = buf.readByteArray()
        assertEquals(viaArray.size, viaSink.size)
        for (i in viaArray.indices) {
            assertEquals(
                viaArray[i], viaSink[i],
                "byte mismatch at index $i (size=${viaArray.size})",
            )
        }
    }
}

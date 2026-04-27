// No upstream analogue — Android instrumentation test for the :android wrapper.
package io.docxkt.android

import android.os.Build
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import io.docxkt.api.document
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumentation test for [saveToDownloads].
 *
 * Requires a real device/emulator. The scoped-storage path on Android
 * 10+ does not need any runtime permission; we exercise that branch
 * exclusively to keep the test self-contained. The pre-Q legacy branch
 * would need `WRITE_EXTERNAL_STORAGE` and runtime permission handling,
 * which is out of scope for an in-library smoke test.
 */
@RunWith(AndroidJUnit4::class)
@SdkSuppress(minSdkVersion = Build.VERSION_CODES.Q)
class DocumentSaveToDownloadsInstrumentedTest {

    @Test
    fun saves_document_to_downloads_and_reads_back_same_bytes() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val doc = document {
            paragraph { text("Instrumented hello — ${System.currentTimeMillis()}") }
        }
        val expected = doc.toByteArray()
        val displayName = "docxkt-instr-${System.currentTimeMillis()}.docx"

        val uri = doc.saveToDownloads(context, displayName)
        try {
            val actual = context.contentResolver.openInputStream(uri).use { stream ->
                requireNotNull(stream) { "openInputStream returned null for $uri" }
                stream.readBytes()
            }
            assertArrayEquals(
                "Round-tripped bytes should equal Document.toByteArray() output",
                expected,
                actual,
            )
            // Basic ZIP-magic sanity: PK\x03\x04 at offset 0.
            assertTrue(
                "Result should start with a ZIP local-file-header signature",
                actual[0] == 0x50.toByte() &&
                    actual[1] == 0x4B.toByte() &&
                    actual[2] == 0x03.toByte() &&
                    actual[3] == 0x04.toByte(),
            )
        } finally {
            context.contentResolver.delete(uri, null, null)
        }
    }
}

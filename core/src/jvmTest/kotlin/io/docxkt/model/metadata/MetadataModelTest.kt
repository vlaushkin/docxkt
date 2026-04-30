// No upstream analogue — synthetic tests for model/metadata.
package io.docxkt.model.metadata

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MetadataModelTest {

    @Test
    fun `CoreProperties effective defaults match upstream literals`() {
        val cp = CoreProperties()
        assertEquals("Un-named", cp.effectiveCreator)
        assertEquals("Un-named", cp.effectiveLastModifiedBy)
        assertEquals(1, cp.effectiveRevision)
    }

    @Test
    fun `CoreProperties uses explicit values when set`() {
        val cp = CoreProperties(
            creator = "Vasily",
            lastModifiedBy = "Vasily",
            revision = 7,
        )
        assertEquals("Vasily", cp.effectiveCreator)
        assertEquals("Vasily", cp.effectiveLastModifiedBy)
        assertEquals(7, cp.effectiveRevision)
    }

    @Test
    fun `CoreProperties default modifiedAt mirrors createdAt`() {
        val ts = "2026-04-27T10:00:00Z"
        val cp = CoreProperties(createdAt = ts)
        assertEquals(ts, cp.modifiedAt)
    }

    @Test
    fun `CustomProperty constants match upstream's hardcoded values`() {
        assertEquals(
            "{D5CDD505-2E9C-101B-9397-08002B2CF9AE}",
            CustomProperty.FORMAT_ID,
        )
        assertEquals(2, CustomProperty.PID_START)
    }

    @Test
    fun `Settings defaults compatibilityVersion to 15`() {
        assertEquals(15, Settings().compatibilityVersion)
    }

    @Test
    fun `Settings null OnOff fields default to expected types`() {
        val s = Settings()
        assertEquals(null, s.trackRevisions)
        assertEquals(null, s.updateFields)
        assertEquals(null, s.defaultTabStop)
        assertEquals(false, s.evenAndOddHeaders)
    }

    @Test
    fun `CompatibilityFlags emits nothing when all flags are false`() {
        val xml = StringBuilder().apply { CompatibilityFlags().appendXml(this) }.toString()
        assertEquals("", xml)
    }

    @Test
    fun `CompatibilityFlags emits each set flag`() {
        val flags = CompatibilityFlags(
            usePrinterMetrics = true,
            doNotSnapToGridInCell = true,
        )
        val xml = StringBuilder().apply { flags.appendXml(this) }.toString()
        assertTrue(xml.contains("<w:usePrinterMetrics/>"))
        assertTrue(xml.contains("<w:doNotSnapToGridInCell/>"))
        // Unset flags must not leak through.
        assertFalse(xml.contains("<w:wpJustification/>"))
    }

    @Test
    fun `CompatibilityFlags emits in upstream-order`() {
        val flags = CompatibilityFlags(
            usePrinterMetrics = true,
            growAutofit = true,
        )
        val xml = StringBuilder().apply { flags.appendXml(this) }.toString()
        // Per upstream's Compatibility class, usePrinterMetrics
        // (line 312) emits BEFORE growAutofit (line 360+).
        val printerIdx = xml.indexOf("<w:usePrinterMetrics")
        val growIdx = xml.indexOf("<w:growAutofit")
        assertTrue(printerIdx in 0 until growIdx)
    }
}

// No upstream analogue — synthetic OnOff three-state coverage for
// the v1.0 API surface.
package io.docxkt.dsl

import io.docxkt.api.document
import io.docxkt.api.toByteArray
import java.util.zip.ZipInputStream
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Asserts that [SettingsScope.evenAndOddHeaders] honours the
 * three-state OnOff contract: `null` (default) emits the upstream-
 * compat `w:val="false"` form, `true` emits the bare OnOff-true
 * form, `false` emits the explicit `w:val="false"` form.
 */
internal class SettingsScopeOnOffTest {

    @Test
    fun `null default emits upstream compat false`() {
        val xml = settingsXml(value = null)
        assertTrue(
            xml.contains("<w:evenAndOddHeaders w:val=\"false\"/>"),
            "null should emit upstream-compat false form, got: $xml",
        )
    }

    @Test
    fun `true emits bare OnOff form`() {
        val xml = settingsXml(value = true)
        assertTrue(
            xml.contains("<w:evenAndOddHeaders/>"),
            "true should emit bare <w:evenAndOddHeaders/>, got: $xml",
        )
    }

    @Test
    fun `false emits explicit val false`() {
        val xml = settingsXml(value = false)
        assertTrue(
            xml.contains("<w:evenAndOddHeaders w:val=\"false\"/>"),
            "false should emit explicit w:val=\"false\", got: $xml",
        )
    }

    private fun settingsXml(value: Boolean?): String {
        val doc = document {
            settings { evenAndOddHeaders = value }
            paragraph { text("hello") }
        }
        val bytes = doc.toByteArray()
        ZipInputStream(bytes.inputStream()).use { zin ->
            while (true) {
                val entry = zin.nextEntry ?: break
                if (entry.name == "word/settings.xml") {
                    return zin.readAllBytes().toString(Charsets.UTF_8)
                }
            }
        }
        error("word/settings.xml not found")
    }
}

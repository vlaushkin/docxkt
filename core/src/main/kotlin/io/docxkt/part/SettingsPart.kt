// Port of: src/file/settings/settings.ts (Settings).
package io.docxkt.part

import io.docxkt.model.metadata.Settings
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.onOff
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/settings.xml` — document-level settings.
 *
 * Always emits `<w:displayBackgroundShape/>` (upstream always
 * pushes this). Always emits `<w:compat>` with a single
 * `<w:compatSetting w:val="…" w:name="compatibilityMode" …/>`.
 * Other upstream-optional knobs (trackRevisions,
 * evenAndOddHeaders, updateFields, defaultTabStop) are emitted
 * only when set.
 *
 * Emission order matches upstream's `Settings` constructor:
 * displayBackgroundShape → trackRevisions → evenAndOddHeaders
 * → updateFields → defaultTabStop → compat.
 *
 * Wire quirk: `<w:evenAndOddHeaders w:val="false"/>` uses OnOff
 * semantics via the [io.docxkt.xml.onOff] helper. `true` emits
 * `<w:evenAndOddHeaders/>` (no attribute).
 */
internal class SettingsPart(
    val settings: Settings,
) {
    val path: String = "word/settings.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            addAll(Namespaces.FOOTER_ROOT_NAMESPACES)
            add("mc:Ignorable" to Namespaces.DOCUMENT_MC_IGNORABLE)
        }.toTypedArray()
        out.openElement("w:settings", *attrs)

        // Upstream always pushes displayBackgroundShape first.
        out.selfClosingElement("w:displayBackgroundShape")

        out.onOff("w:trackRevisions", settings.trackRevisions)
        out.onOff("w:evenAndOddHeaders", settings.evenAndOddHeaders)
        out.onOff("w:updateFields", settings.updateFields)
        settings.defaultTabStop?.let {
            out.selfClosingElement("w:defaultTabStop", "w:val" to it.toString())
        }

        out.openElement("w:compat")
        // Attribute order on <w:compatSetting> matches upstream's
        // BuilderElement property-map order: version (w:val) → name
        // → uri.
        out.selfClosingElement(
            "w:compatSetting",
            "w:val" to settings.compatibilityVersion.toString(),
            "w:name" to "compatibilityMode",
            "w:uri" to Namespaces.WORD_2010_URI,
        )
        // Legacy compat flags after compatSetting.
        settings.compatibilityFlags.appendXml(out)
        out.closeElement("w:compat")

        out.closeElement("w:settings")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

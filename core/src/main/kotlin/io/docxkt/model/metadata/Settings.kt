// Port of: src/file/settings/settings.ts (Settings).
// Upstream's ISettingsOptions carries many more knobs; add as needed.
package io.docxkt.model.metadata

/**
 * Document-level settings that land in `word/settings.xml`.
 *
 * Upstream always emits `<w:displayBackgroundShape/>` and
 * `<w:compat><w:compatSetting w:val="15"
 * w:name="compatibilityMode" w:uri=".../office/word"/></w:compat>`
 * unconditionally. We match. Everything else is DSL-controlled
 * and suppressed when unset — except [evenAndOddHeaders] which
 * upstream's `file.ts` hardcodes to emit with `w:val="false"`
 * when the caller doesn't explicitly turn it on. We default to
 * `false` for the same wire behaviour.
 *
 * [compatibilityVersion] maps to the `w:val` on
 * `<w:compatSetting>`; upstream defaults to 15 (Word 2013+).
 */
internal data class Settings(
    val compatibilityVersion: Int = 15,
    val defaultTabStop: Int? = null,
    val evenAndOddHeaders: Boolean = false,
    val trackRevisions: Boolean? = null,
    val updateFields: Boolean? = null,
    val compatibilityFlags: CompatibilityFlags = CompatibilityFlags(),
)

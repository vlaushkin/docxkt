// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.metadata.CompatibilityFlags
import io.docxkt.model.metadata.Settings

/**
 * Configure document settings emitted to `word/settings.xml`.
 *
 * Unset fields default to upstream's behaviour:
 * - `compatibilityVersion = 15` (Word 2013+)
 * - `evenAndOddHeaders = null` ⇒ wire emits the
 *   `<w:evenAndOddHeaders w:val="false"/>` default form, mirroring
 *   upstream's `file.ts` hardcoded default. Set to `true` for the
 *   attribute-free `<w:evenAndOddHeaders/>` (OnOff true) form;
 *   `false` writes `w:val="false"` explicitly (byte-equal to
 *   leaving null, but distinguishable in code reviews).
 * - `trackRevisions`, `updateFields`, `defaultTabStop` suppressed
 *   when unset.
 */
@DocxktDsl
public class SettingsScope internal constructor(
    internal val context: DocumentContext,
) {
    public var compatibilityVersion: Int = 15
    public var defaultTabStop: Int? = null

    /**
     * Three-state OnOff control over `<w:evenAndOddHeaders/>` in
     * `word/settings.xml`. `null` (default) emits the upstream-compat
     * `w:val="false"` form; `true` emits the OnOff-true bare form;
     * `false` emits an explicit `w:val="false"` (functionally
     * identical to null at the wire). Auto-set to `true` by the
     * Document scope when any section uses an EVEN header/footer
     * — overriding a `null` here, but not an explicit `false`.
     */
    public var evenAndOddHeaders: Boolean? = null
    public var trackRevisions: Boolean? = null
    public var updateFields: Boolean? = null
    public var compatibilityFlags: CompatibilityFlags = CompatibilityFlags()

    /**
     * Set legacy `<w:compat>` flags. Pass a fully-constructed
     * [CompatibilityFlags] (use named arguments to flip just the
     * flags you want). Replaces any prior value.
     */
    public fun compatibility(flags: CompatibilityFlags) {
        compatibilityFlags = flags
    }

    internal fun build() {
        context.settings = Settings(
            compatibilityVersion = compatibilityVersion,
            defaultTabStop = defaultTabStop,
            // Null collapses to upstream's hardcoded false default
            // — preserves byte-equal wire when the user says nothing.
            evenAndOddHeaders = evenAndOddHeaders ?: false,
            trackRevisions = trackRevisions,
            updateFields = updateFields,
            compatibilityFlags = compatibilityFlags,
        )
    }
}

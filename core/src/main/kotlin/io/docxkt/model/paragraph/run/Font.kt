// Port of: src/file/paragraph/run/run-fonts.ts (createRunFonts).
package io.docxkt.model.paragraph.run

/**
 * `<w:rFonts>` content. OOXML lets different fonts apply to different
 * character ranges (ASCII, high ANSI, complex script, East Asian).
 *
 * Two construction forms mirror upstream:
 * - [single] — one name for every range; upstream `createRunFonts("Arial")`
 *   emits `w:ascii`, `w:cs`, `w:eastAsia`, `w:hAnsi` all equal to "Arial".
 * - [perScript] — each range may be set independently; unset ranges
 *   skip their attribute (upstream `BuilderElement` omits `undefined`
 *   attributes).
 */
public class Font private constructor(
    internal val ascii: String?,
    internal val hAnsi: String?,
    internal val cs: String?,
    internal val eastAsia: String?,
    internal val hint: String?,
) {
    public companion object {
        /** One font name applied to all four ranges. */
        public fun single(name: String, hint: String? = null): Font =
            Font(ascii = name, hAnsi = name, cs = name, eastAsia = name, hint = hint)

        /** Per-range font names; unset ranges omit their attribute. */
        public fun perScript(
            ascii: String? = null,
            hAnsi: String? = null,
            cs: String? = null,
            eastAsia: String? = null,
            hint: String? = null,
        ): Font = Font(ascii = ascii, hAnsi = hAnsi, cs = cs, eastAsia = eastAsia, hint = hint)
    }
}

// Port of: src/file/xml-components/on-off-element.ts (OnOffElement).
package io.docxkt.xml

/**
 * Emit an OOXML "OnOff" element.
 *
 * OnOff is the format used by boolean wordprocessingML properties such as
 * `<w:b>`, `<w:i>`, `<w:strike>`:
 *
 *  - `null` (unset, "inherit"): emit nothing.
 *  - `true`: emit `<name/>` — attribute-free form.
 *  - `false`: emit `<name w:val="false"/>`.
 *
 * **Never** emit `<name w:val="true"/>`. OOXML accepts it, but upstream
 * never produces it and our fixture diffs will turn red. See
 * `CLAUDE.md` §"OOXML gotchas that bite".
 */
internal fun Appendable.onOff(name: String, value: Boolean?) {
    when (value) {
        null -> return
        true -> selfClosingElement(name)
        false -> selfClosingElement(name, "w:val" to "false")
    }
}

// Port of: src/file/paragraph/run/run-components/symbol.ts (Symbol).
package io.docxkt.model.symbol

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:sym w:char="…" w:font="…"/>` — a non-Unicode glyph
 * from a symbol font.
 *
 * Attribute order: `w:char` first, then `w:font`. Matches
 * upstream's `SymbolAttributes.xmlKeys` declaration order.
 */
internal class Symbol(
    val char: String,
    val font: String = "Wingdings",
) : XmlComponent("w:sym") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:sym",
            "w:char" to char,
            "w:font" to font,
        )
    }
}

// Port of: src/file/fonts/font-table.ts (createFontTable) +
// src/file/fonts/create-regular-font.ts (createRegularFont).
package io.docxkt.part

import io.docxkt.model.font.EmbeddedFont
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/fontTable.xml` — the font dictionary for the document. When
 * the DSL registers no embedded fonts, a self-closing `<w:fonts/>`
 * is emitted (upstream's empty form). When fonts ARE registered,
 * each becomes a `<w:font>` child with the upstream `createRegularFont`
 * shape: charset / family auto / pitch variable / fixed sig / and
 * `<w:embedRegular r:id="rIdN" w:fontKey="{...}"/>`.
 *
 * The signature values usb0..csb1 are HARDCODED to upstream's
 * `createRegularFont` defaults (commodity ANSI font ranges) — upstream
 * doesn't probe the TTF's OS/2 table, and neither do we.
 */
internal class FontTablePart(
    private val embeds: List<EmbeddedFontEmbed> = emptyList(),
) {
    val path: String = "word/fontTable.xml"

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            addAll(Namespaces.FONT_TABLE_ROOT_NAMESPACES)
            add("mc:Ignorable" to Namespaces.FONT_TABLE_MC_IGNORABLE)
        }.toTypedArray()
        if (embeds.isEmpty()) {
            out.selfClosingElement("w:fonts", *attrs)
            return
        }
        out.openElement("w:fonts", *attrs)
        for (embed in embeds) embed.appendXml(out)
        out.closeElement("w:fonts")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

/**
 * One `<w:font>` entry inside `<w:fonts>` — built lazily by
 * `Document.buildDocument()` once the font's relationship rId is
 * allocated.
 */
internal data class EmbeddedFontEmbed(
    val font: EmbeddedFont,
    val rid: String,
) {
    fun appendXml(out: Appendable) {
        out.openElement("w:font", "w:name" to font.name)
        out.selfClosingElement("w:charset", "w:val" to font.characterSet.wire)
        out.selfClosingElement("w:family", "w:val" to "auto")
        out.selfClosingElement("w:pitch", "w:val" to "variable")
        out.selfClosingElement(
            "w:sig",
            "w:usb0" to "E0002AFF",
            "w:usb1" to "C000247B",
            "w:usb2" to "00000009",
            "w:usb3" to "00000000",
            "w:csb0" to "000001FF",
            "w:csb1" to "00000000",
        )
        out.selfClosingElement(
            "w:embedRegular",
            "r:id" to rid,
            "w:fontKey" to "{${font.fontKey}}",
        )
        out.closeElement("w:font")
    }
}

// Port of: src/file/styles/style/style.ts (base) + paragraph-style.ts
// + character-style.ts (wire order on the same `<w:style>` element).
package io.docxkt.model.style

import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.run.RunProperties
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.onOff
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * A single `<w:style w:type="…" w:styleId="…">` entry for
 * `word/styles.xml`.
 *
 * Child order mirrors upstream's `Style.ts`:
 * `w:name → w:basedOn → w:next → w:link → w:uiPriority →
 * w:semiHidden → w:unhideWhenUsed → w:qFormat → w:pPr → w:rPr`.
 *
 * Attribute order inside the `<w:style>` element itself is
 * `w:type w:styleId`, matching upstream.
 *
 * Character styles must not carry a [paragraphProperties] — OOXML
 * rejects `<w:pPr>` inside `<w:style w:type="character">`. The DSL
 * enforces this; the model trusts its callers.
 *
 * `paragraphProperties` and `runProperties` inherit
 * [io.docxkt.xml.IgnoreIfEmptyXmlComponent] behaviour — an empty
 * props container simply doesn't emit. Setting `bold = true` on a
 * freshly-constructed `RunProperties` produces `<w:rPr><w:b/>…</w:rPr>`;
 * omitting every field produces no `<w:rPr>` at all. We match
 * upstream's "no empty rPr" convention.
 */
internal class Style(
    val type: StyleType,
    val id: String,
    val name: String? = null,
    val basedOn: String? = null,
    val next: String? = null,
    val link: String? = null,
    val uiPriority: Int? = null,
    val semiHidden: Boolean? = null,
    val unhideWhenUsed: Boolean? = null,
    val quickFormat: Boolean? = null,
    val paragraphProperties: ParagraphProperties? = null,
    val runProperties: RunProperties? = null,
) : XmlComponent("w:style") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:style",
            "w:type" to type.wire,
            "w:styleId" to id,
        )
        name?.let { out.selfClosingElement("w:name", "w:val" to it) }
        basedOn?.let { out.selfClosingElement("w:basedOn", "w:val" to it) }
        next?.let { out.selfClosingElement("w:next", "w:val" to it) }
        link?.let { out.selfClosingElement("w:link", "w:val" to it) }
        uiPriority?.let {
            out.selfClosingElement("w:uiPriority", "w:val" to it.toString())
        }
        out.onOff("w:semiHidden", semiHidden)
        out.onOff("w:unhideWhenUsed", unhideWhenUsed)
        out.onOff("w:qFormat", quickFormat)
        paragraphProperties?.appendXml(out)
        runProperties?.appendXml(out)
        out.closeElement("w:style")
    }
}

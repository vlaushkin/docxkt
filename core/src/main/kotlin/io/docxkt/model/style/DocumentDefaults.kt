// Port of: src/file/styles/defaults/document-defaults.ts +
//          paragraph-properties.ts + run-properties.ts.
package io.docxkt.model.style

import io.docxkt.model.paragraph.ParagraphProperties
import io.docxkt.model.paragraph.run.RunProperties
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `<w:docDefaults>` — document-wide default formatting that
 * applies to every paragraph and run unless overridden.
 *
 * Emits `<w:rPrDefault><w:rPr>...</w:rPr></w:rPrDefault>` and/or
 * `<w:pPrDefault><w:pPr>...</w:pPr></w:pPrDefault>` children.
 * Either side may be null — the wrapper element is always emitted
 * when DocumentDefaults is registered (matches upstream's
 * unconditional `<w:rPrDefault>` + `<w:pPrDefault>` emission even
 * when their inner pPr/rPr are empty).
 */
internal class DocumentDefaults(
    val runDefaults: RunProperties? = null,
    val paragraphDefaults: ParagraphProperties? = null,
) : XmlComponent("w:docDefaults") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:docDefaults")
        out.openElement("w:rPrDefault")
        runDefaults?.appendXml(out)
        out.closeElement("w:rPrDefault")
        out.openElement("w:pPrDefault")
        paragraphDefaults?.appendXml(out)
        out.closeElement("w:pPrDefault")
        out.closeElement("w:docDefaults")
    }
}

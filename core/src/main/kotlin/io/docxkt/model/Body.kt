// Port of: src/file/document/body/body.ts
package io.docxkt.model

import io.docxkt.model.section.SectionProperties
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * A `<w:body>` element — the single child of `<w:document>` holding all
 * block-level content.
 *
 * Block content (paragraphs, tables) is emitted first, then the
 * document's `<w:sectPr>` as the last child — OOXML requires `sectPr`
 * to be the final element. Upstream omits `<w:sectPr>` entirely when
 * the document has `sections=[]` (no blocks AND no per-section
 * configuration) and emits `<w:body/>` self-closed; pass
 * [sectionProperties] = `null` for that case.
 */
internal class Body(
    val children: List<XmlComponent>,
    val sectionProperties: SectionProperties? = SectionProperties.default(),
) : XmlComponent("w:body") {

    override fun appendXml(out: Appendable) {
        if (children.isEmpty() && sectionProperties == null) {
            out.selfClosingElement(elementName)
            return
        }
        out.openElement(elementName)
        for (child in children) {
            child.appendXml(out)
        }
        sectionProperties?.appendXml(out)
        out.closeElement(elementName)
    }
}

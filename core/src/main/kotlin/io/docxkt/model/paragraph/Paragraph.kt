// Port of: src/file/paragraph/paragraph.ts (L109-L144)
package io.docxkt.model.paragraph

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * A `<w:p>` paragraph: an optional `<w:pPr>` followed by a sequence of
 * inline children.
 *
 * [ParagraphProperties] is an [io.docxkt.xml.IgnoreIfEmptyXmlComponent],
 * so an all-null `properties` argument emits no `<w:pPr>`.
 *
 * When properties are present they are emitted first, enforcing the
 * OOXML rule that `<w:pPr>` precedes child content.
 *
 * [children] is typed as `List<XmlComponent>` to admit both `<w:r>`
 * runs and non-run inline elements (`<w:hyperlink>`, bookmark markers,
 * etc.). Same looseness pattern as `Body.children`.
 */
internal class Paragraph(
    val children: List<XmlComponent>,
    val properties: ParagraphProperties? = null,
) : XmlComponent("w:p") {

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        properties?.appendXml(out)
        for (child in children) {
            child.appendXml(out)
        }
        out.closeElement(elementName)
    }
}

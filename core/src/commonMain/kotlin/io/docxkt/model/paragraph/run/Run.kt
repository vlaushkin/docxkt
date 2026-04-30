// Port of: src/file/paragraph/run/run.ts (L169-L223)
package io.docxkt.model.paragraph.run

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * A `<w:r>` run: a sequence of formatted text and line/page breaks
 * inside a paragraph.
 *
 * The optional [properties] container is emitted first (when non-empty),
 * matching the OOXML rule that `<w:rPr>` must precede child content. An
 * all-null `properties` is equivalent to no properties and produces no
 * `<w:rPr>` tag thanks to [RunProperties]' `IgnoreIfEmptyXmlComponent`
 * base class.
 *
 * [children] is `List<XmlComponent>` so [Text] and [Break] can sit
 * side-by-side. The public DSL (`RunScope`) only admits the legitimate
 * child types, so the looser model-layer type costs nothing.
 */
internal class Run(
    val children: List<XmlComponent>,
    val properties: RunProperties? = null,
) : XmlComponent("w:r") {

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        properties?.appendXml(out)
        for (child in children) {
            child.appendXml(out)
        }
        out.closeElement(elementName)
    }
}

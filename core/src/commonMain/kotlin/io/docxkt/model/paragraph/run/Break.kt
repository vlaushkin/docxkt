// Port of: src/file/paragraph/run/break.ts (Break component).
package io.docxkt.model.paragraph.run

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * Type of `<w:br>` break.
 *
 * - [LINE] — soft line break, no `w:type` attribute. Upstream's
 *   default and the cheapest regression to miss.
 * - [PAGE] — hard page break (`w:type="page"`).
 * - [TEXT_WRAPPING] — explicit `w:type="textWrapping"` form.
 * - [COLUMN] — column break (`w:type="column"`). Meaningful only
 *   inside a `<w:cols>`-configured section.
 */
public enum class BreakType(internal val wire: String?) {
    LINE(null),
    PAGE("page"),
    TEXT_WRAPPING("textWrapping"),
    COLUMN("column"),
}

/**
 * A `<w:br/>` element inside a `<w:r>`.
 *
 * Sibling of [Text] — both are legitimate children of [Run]. The
 * default is a soft line break (`<w:br/>`); page breaks use
 * [BreakType.PAGE] and emit `<w:br w:type="page"/>`.
 */
internal class Break(
    val type: BreakType = BreakType.LINE,
) : XmlComponent("w:br") {

    override fun appendXml(out: Appendable) {
        if (type.wire == null) {
            out.selfClosingElement(elementName)
        } else {
            out.selfClosingElement(elementName, "w:type" to type.wire)
        }
    }
}

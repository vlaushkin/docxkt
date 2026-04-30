// Port of: src/file/paragraph/run/empty-children.ts
//          (Tab, SoftHyphen, NoBreakHyphen).
package io.docxkt.model.paragraph.run

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:tab/>` — tab character. Distinct from `<w:tabs>`
 * (paragraph-level tab-stop definitions); this is the actual
 * control character that advances to the next defined stop.
 */
internal class Tab : XmlComponent("w:tab") {
    override fun appendXml(out: Appendable) {
        out.selfClosingElement("w:tab")
    }
}

/**
 * `<w:softHyphen/>` — optional hyphen. Renders as nothing
 * unless the word containing it needs to wrap, at which point
 * it renders as a hyphen at the break point.
 */
internal class SoftHyphen : XmlComponent("w:softHyphen") {
    override fun appendXml(out: Appendable) {
        out.selfClosingElement("w:softHyphen")
    }
}

/**
 * `<w:noBreakHyphen/>` — visible hyphen that prevents wrapping
 * at that position.
 */
internal class NoBreakHyphen : XmlComponent("w:noBreakHyphen") {
    override fun appendXml(out: Appendable) {
        out.selfClosingElement("w:noBreakHyphen")
    }
}

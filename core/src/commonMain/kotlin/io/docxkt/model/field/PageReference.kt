// Port of: src/file/paragraph/run/page-number.ts (PageReference shape)
package io.docxkt.model.field

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `PAGEREF <bookmarkName>` complex field — emits a single `<w:r>`
 * with the begin/instrText/end chain (no separate marker, no cached
 * text). Always `w:dirty="true"` so Word recomputes the page number
 * when the document opens.
 *
 * Differs from [ComplexField] in two ways:
 *   1. No `<w:fldChar w:fldCharType="separate"/>` marker.
 *   2. No `<w:t>` cached value.
 *
 * Reference: upstream emits the same 3-marker shape for
 * `new PageReference(bookmarkName)` in
 * `src/file/paragraph/run/page-number.ts`.
 */
internal class PageReference(
    val bookmarkName: String,
) : XmlComponent("w:r") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:r")
        out.selfClosingElement(
            "w:fldChar",
            "w:fldCharType" to "begin",
            "w:dirty" to "true",
        )
        out.openElement("w:instrText", "xml:space" to "preserve")
        out.append("PAGEREF ")
        out.append(XmlEscape.escapeText(bookmarkName))
        out.closeElement("w:instrText")
        out.selfClosingElement("w:fldChar", "w:fldCharType" to "end")
        out.closeElement("w:r")
    }
}

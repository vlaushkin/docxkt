// Port of: src/file/paragraph/formatting/border.ts (Border, L68-L92).
package io.docxkt.model.paragraph

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.writeBorderSide
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `<w:pBdr>` — paragraph borders.
 *
 * Five sides (top, bottom, left, right, between). Upstream's
 * emission order is **not** the XSD sequence (top, left, bottom,
 * right, between) — it's `top → bottom → left → right → between`.
 * We mirror upstream so fixture diffs stay byte-stable; if you find
 * yourself tempted to "fix" the order to XSD-canonical, don't.
 *
 * `<w:bar>` (the sixth XSD side) is out of scope — add only when a
 * consumer needs it.
 */
internal class ParagraphBorders(
    val top: BorderSide? = null,
    val bottom: BorderSide? = null,
    val left: BorderSide? = null,
    val right: BorderSide? = null,
    val between: BorderSide? = null,
) : IgnoreIfEmptyXmlComponent("w:pBdr") {

    override fun isEmpty(): Boolean =
        top == null && bottom == null && left == null && right == null && between == null

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:pBdr")
        top?.let { writeBorderSide(out, "w:top", it) }
        bottom?.let { writeBorderSide(out, "w:bottom", it) }
        left?.let { writeBorderSide(out, "w:left", it) }
        right?.let { writeBorderSide(out, "w:right", it) }
        between?.let { writeBorderSide(out, "w:between", it) }
        out.closeElement("w:pBdr")
    }
}

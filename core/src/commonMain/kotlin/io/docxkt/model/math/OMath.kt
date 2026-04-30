// Port of: src/file/paragraph/math/math.ts (`<m:oMath>`).
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:oMath>` — top-level inline math container. Sits as a
 * paragraph child alongside `<w:r>` / `<w:hyperlink>` /
 * `<w:bookmarkStart>` etc.
 *
 * Display-mode `<m:oMathPara>` is not yet supported.
 */
internal class OMath(
    val children: List<MathComponent>,
) : XmlComponent("m:oMath") {

    override fun appendXml(out: Appendable) {
        out.append("<m:oMath>")
        for (child in children) (child as XmlComponent).appendXml(out)
        out.append("</m:oMath>")
    }
}

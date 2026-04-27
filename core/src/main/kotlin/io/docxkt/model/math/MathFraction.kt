// Port of: src/file/paragraph/math/fraction/math-fraction.ts +
//          math-numerator.ts + math-denominator.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:f>` fraction — numerator over denominator.
 *
 * Upstream's `MathFraction` constructor omits `<m:fPr>`
 * entirely when no fraction-property options are provided,
 * relying on Word's stacked-fraction default. We match.
 */
internal class MathFraction(
    val numerator: List<MathComponent>,
    val denominator: List<MathComponent>,
) : XmlComponent("m:f"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:f>")
        out.append("<m:num>")
        for (child in numerator) (child as XmlComponent).appendXml(out)
        out.append("</m:num>")
        out.append("<m:den>")
        for (child in denominator) (child as XmlComponent).appendXml(out)
        out.append("</m:den>")
        out.append("</m:f>")
    }
}

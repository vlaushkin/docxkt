// Port of: src/file/paragraph/math/n-ary/math-limit-lower.ts +
//          math-limit-upper.ts + math-limit.ts +
//          math-base.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:limLow>` — lower-limit math structure: base + limit
 * underneath. Used for `lim x→0` style.
 */
internal class MathLimitLower(
    val base: List<MathComponent>,
    val limit: List<MathComponent>,
) : XmlComponent("m:limLow"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:limLow>")
        out.append("<m:e>")
        for (child in base) (child as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:lim>")
        for (child in limit) (child as XmlComponent).appendXml(out)
        out.append("</m:lim>")
        out.append("</m:limLow>")
    }
}

/**
 * `<m:limUpp>` — upper-limit math structure: base + limit
 * over the base. Used e.g. for x⃗ overhead annotations.
 */
internal class MathLimitUpper(
    val base: List<MathComponent>,
    val limit: List<MathComponent>,
) : XmlComponent("m:limUpp"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:limUpp>")
        out.append("<m:e>")
        for (child in base) (child as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:lim>")
        for (child in limit) (child as XmlComponent).appendXml(out)
        out.append("</m:lim>")
        out.append("</m:limUpp>")
    }
}

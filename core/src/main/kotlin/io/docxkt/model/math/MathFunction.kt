// Port of: src/file/paragraph/math/function/math-function.ts +
//          math-function-name.ts + math-function-properties.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:func>` — named function application (sin / cos / log /
 * etc.).
 *
 * Body order: `m:funcPr (empty) → m:fName → m:e`.
 *
 * [name] and [children] are both lists of math components,
 * so the name can itself be a complex expression (e.g.
 * `log` with a base subscript) — though the most common
 * case is a single `MathRun("sin")`.
 */
internal class MathFunction(
    val name: List<MathComponent>,
    val children: List<MathComponent>,
) : XmlComponent("m:func"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:func>")
        out.append("<m:funcPr/>")
        out.append("<m:fName>")
        for (c in name) (c as XmlComponent).appendXml(out)
        out.append("</m:fName>")
        out.append("<m:e>")
        for (c in children) (c as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("</m:func>")
    }
}

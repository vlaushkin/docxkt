// Port of: src/file/paragraph/math/radical/math-radical.ts +
//          math-radical-properties.ts + math-degree.ts +
//          math-degree-hide.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:rad>` radical (square / nth root).
 *
 * Wire shape:
 * ```
 * <m:rad>
 *   <m:radPr>
 *     [<m:degHide m:val="1"/>]?  -- present when [degree] is null
 *   </m:radPr>
 *   <m:deg>[…degree children…]?</m:deg>
 *   <m:e>…radicand children…</m:e>
 * </m:rad>
 * ```
 *
 * When [degree] is null (square root), upstream emits
 * `<m:radPr><m:degHide m:val="1"/></m:radPr>` and an empty
 * `<m:deg/>`. When [degree] is provided (n-th root),
 * upstream emits `<m:radPr/>` (empty) and the degree
 * inside `<m:deg>`.
 */
internal class MathRadical(
    val children: List<MathComponent>,
    val degree: List<MathComponent>? = null,
) : XmlComponent("m:rad"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:rad>")
        if (degree == null) {
            out.append("<m:radPr><m:degHide m:val=\"1\"/></m:radPr>")
            out.append("<m:deg/>")
        } else {
            out.append("<m:radPr/>")
            out.append("<m:deg>")
            for (child in degree) (child as XmlComponent).appendXml(out)
            out.append("</m:deg>")
        }
        out.append("<m:e>")
        for (child in children) (child as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("</m:rad>")
    }
}

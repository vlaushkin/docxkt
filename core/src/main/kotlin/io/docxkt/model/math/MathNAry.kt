// Port of: src/file/paragraph/math/n-ary/math-sum.ts +
//          math-integral.ts + math-n-ary-properties.ts +
//          math-accent-character.ts + math-limit-location.ts +
//          math-sub-script.ts + math-super-script.ts +
//          math-sub-script-hide.ts + math-super-script-hide.ts +
//          math-base.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape

/**
 * `<m:limLoc>` value — where limits sit relative to the
 * n-ary operator.
 */
public enum class MathLimitLocation(internal val wire: String) {
    /** Limits stack above/below the operator (default for ∑/∏). */
    UNDER_OVER("undOvr"),

    /** Limits sit as sub/superscript (default for ∫). */
    SUB_SUP("subSup"),
}

/**
 * `<m:nary>` — generic n-ary operator (sum / product /
 * integral / etc.). Optional sub/super-script limits +
 * required body.
 *
 * [accentChar] is null for integrals (upstream's `accent=""`
 * branch — no `<m:chr>` emitted) and the operator glyph
 * (`"∑"` / `"∏"` / etc.) for explicit operators.
 *
 * `<m:naryPr>` child order:
 *   chr (if accentChar != null) → limLoc → supHide (if no
 *   superScript) → subHide (if no subScript).
 *
 * `<m:nary>` body order:
 *   naryPr → sub (if present) → sup (if present) → e.
 */
internal class MathNAry(
    val children: List<MathComponent>,
    val subScript: List<MathComponent>? = null,
    val superScript: List<MathComponent>? = null,
    val accentChar: String? = null,
    val limitLocation: MathLimitLocation = MathLimitLocation.UNDER_OVER,
) : XmlComponent("m:nary"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:nary>")

        // naryPr
        out.append("<m:naryPr>")
        accentChar?.let {
            out.append("<m:chr m:val=\"")
            out.append(XmlEscape.escapeAttributeValue(it))
            out.append("\"/>")
        }
        out.append("<m:limLoc m:val=\"")
        out.append(limitLocation.wire)
        out.append("\"/>")
        if (superScript == null) out.append("<m:supHide m:val=\"1\"/>")
        if (subScript == null) out.append("<m:subHide m:val=\"1\"/>")
        out.append("</m:naryPr>")

        // body
        if (subScript != null) {
            out.append("<m:sub>")
            for (child in subScript) (child as XmlComponent).appendXml(out)
            out.append("</m:sub>")
        }
        if (superScript != null) {
            out.append("<m:sup>")
            for (child in superScript) (child as XmlComponent).appendXml(out)
            out.append("</m:sup>")
        }
        out.append("<m:e>")
        for (child in children) (child as XmlComponent).appendXml(out)
        out.append("</m:e>")

        out.append("</m:nary>")
    }

    companion object {
        public const val SUM_CHAR: String = "∑"
        public const val PRODUCT_CHAR: String = "∏"
    }
}

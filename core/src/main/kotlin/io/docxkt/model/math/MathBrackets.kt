// Port of: src/file/paragraph/math/brackets/math-round-brackets.ts +
//          math-square-brackets.ts + math-curly-brackets.ts +
//          math-angled-brackets.ts + math-bracket-properties.ts +
//          math-beginning-character.ts + math-ending-char.ts.
//
// Single class covers all bracket variants — only the
// `<m:begChr>` / `<m:endChr>` characters differ on the wire.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape

/**
 * `<m:d>` delimiter — content surrounded by paired bracket
 * characters (parentheses, square, curly, angled, …).
 *
 * When both [begin] and [end] are null, upstream emits an
 * empty `<m:dPr/>` and uses Word's default (round
 * parentheses). Setting either character switches to the
 * configured pair.
 *
 * Mirrors upstream's `MathRoundBrackets` (no chars) /
 * `MathSquareBrackets("[" / "]")` / `MathCurlyBrackets("{" /
 * "}")` / `MathAngledBrackets("〈" / "〉")` shape via the
 * `createMathBracketProperties` factory.
 */
internal class MathBrackets(
    val children: List<MathComponent>,
    val begin: String? = null,
    val end: String? = null,
) : XmlComponent("m:d"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:d>")
        if (begin == null && end == null) {
            out.append("<m:dPr/>")
        } else {
            out.append("<m:dPr>")
            begin?.let {
                out.append("<m:begChr m:val=\"")
                out.append(XmlEscape.escapeAttributeValue(it))
                out.append("\"/>")
            }
            end?.let {
                out.append("<m:endChr m:val=\"")
                out.append(XmlEscape.escapeAttributeValue(it))
                out.append("\"/>")
            }
            out.append("</m:dPr>")
        }
        out.append("<m:e>")
        for (child in children) (child as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("</m:d>")
    }
}

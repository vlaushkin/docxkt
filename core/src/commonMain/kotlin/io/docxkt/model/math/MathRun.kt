// Port of: src/file/paragraph/math/math-run.ts +
//          src/file/paragraph/math/math-text.ts.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape

/**
 * `<m:r><m:t>text</m:t></m:r>` — math run with text body.
 *
 * Note `<m:t>` does NOT carry `xml:space="preserve"` —
 * upstream's `MathText` constructor omits it (unlike `<w:t>`
 * which always emits preserve). We match.
 */
internal class MathRun(
    val text: String,
) : XmlComponent("m:r"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:r><m:t>")
        out.append(XmlEscape.escapeText(text))
        out.append("</m:t></m:r>")
    }
}

// Port of: src/file/paragraph/math/script/super-script/* +
//          sub-script/* + pre-sub-super-script/* + sub-super-script/*.
//
// All four script variants share the same shape: a *Pr empty
// self-closed properties element, then the base/sub/sup
// children in upstream-canonical order.
package io.docxkt.model.math

import io.docxkt.xml.XmlComponent

/**
 * `<m:sSup>` — base with superscript (e.g. `x²`).
 * Body order: `m:sSupPr → m:e → m:sup`.
 */
internal class MathSuperScript(
    val children: List<MathComponent>,
    val superScript: List<MathComponent>,
) : XmlComponent("m:sSup"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:sSup>")
        out.append("<m:sSupPr/>")
        out.append("<m:e>")
        for (c in children) (c as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:sup>")
        for (c in superScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sup>")
        out.append("</m:sSup>")
    }
}

/**
 * `<m:sSub>` — base with subscript (e.g. `xᵢ`).
 * Body order: `m:sSubPr → m:e → m:sub`.
 */
internal class MathSubScript(
    val children: List<MathComponent>,
    val subScript: List<MathComponent>,
) : XmlComponent("m:sSub"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:sSub>")
        out.append("<m:sSubPr/>")
        out.append("<m:e>")
        for (c in children) (c as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:sub>")
        for (c in subScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sub>")
        out.append("</m:sSub>")
    }
}

/**
 * `<m:sPre>` — pre-sub and pre-super script. Common in
 * tensor notation: `ⁱTⱼ` style.
 *
 * Body order: `m:sPrePr → m:e → m:sub → m:sup`. Note: base
 * comes BEFORE the scripts even though they render to the
 * left of it; upstream emits in this order regardless.
 */
internal class MathPreSubSuperScript(
    val children: List<MathComponent>,
    val subScript: List<MathComponent>,
    val superScript: List<MathComponent>,
) : XmlComponent("m:sPre"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:sPre>")
        out.append("<m:sPrePr/>")
        out.append("<m:e>")
        for (c in children) (c as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:sub>")
        for (c in subScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sub>")
        out.append("<m:sup>")
        for (c in superScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sup>")
        out.append("</m:sPre>")
    }
}

/**
 * `<m:sSubSup>` — combined sub and super on the same base
 * (e.g. `xᵢ²`).
 * Body order: `m:sSubSupPr → m:e → m:sub → m:sup`.
 */
internal class MathSubSuperScript(
    val children: List<MathComponent>,
    val subScript: List<MathComponent>,
    val superScript: List<MathComponent>,
) : XmlComponent("m:sSubSup"), MathComponent {

    override fun appendXml(out: Appendable) {
        out.append("<m:sSubSup>")
        out.append("<m:sSubSupPr/>")
        out.append("<m:e>")
        for (c in children) (c as XmlComponent).appendXml(out)
        out.append("</m:e>")
        out.append("<m:sub>")
        for (c in subScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sub>")
        out.append("<m:sup>")
        for (c in superScript) (c as XmlComponent).appendXml(out)
        out.append("</m:sup>")
        out.append("</m:sSubSup>")
    }
}

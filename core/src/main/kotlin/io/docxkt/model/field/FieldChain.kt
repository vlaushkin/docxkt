// Port of: src/file/paragraph/run/field.ts (Begin/Separate/End)
//        + src/file/paragraph/run/page-number.ts (Page, NumberOfPages,
//          NumberOfPagesSection, CurrentSection).
package io.docxkt.model.field

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:fldChar w:fldCharType="..."/>` — a single marker in a complex
 * field chain. Used as an inline child of `<w:r>` so the field can
 * share run-level formatting with surrounding text — mirrors
 * upstream's `TextRun({ children: [..., PageNumber.CURRENT] })`
 * expansion in `run.ts`.
 */
internal class FieldChar(
    val type: FieldCharType,
    val dirty: Boolean = false,
) : XmlComponent("w:fldChar") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:fldChar",
            "w:fldCharType" to type.wire,
            "w:dirty" to if (dirty) "true" else null,
        )
    }
}

/**
 * `<w:instrText xml:space="preserve">CODE</w:instrText>` — the
 * field-code instruction emitted between begin and separate
 * markers. Always with `xml:space="preserve"`, matching upstream's
 * `TextAttributes({ space: SpaceType.PRESERVE })` in `page-number.ts`.
 */
internal class InstrText(
    val instruction: String,
) : XmlComponent("w:instrText") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:instrText", "xml:space" to "preserve")
        out.append(XmlEscape.escapeText(instruction))
        out.closeElement("w:instrText")
    }
}

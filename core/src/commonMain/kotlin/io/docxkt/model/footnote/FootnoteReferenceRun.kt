// Port of: src/file/footnotes/footnote/run/reference-run.ts
// (FootnoteReferenceRun + FootnoteReference).
package io.docxkt.model.footnote

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * A paragraph child that emits:
 *
 * ```
 * <w:r>
 *   <w:rPr><w:rStyle w:val="FootnoteReference"/></w:rPr>
 *   <w:footnoteReference w:id="N"/>
 * </w:r>
 * ```
 *
 * Used inline in body paragraphs to point at a footnote in
 * `word/footnotes.xml`.
 *
 * Endnote variant ([EndnoteReferenceRun]) has identical shape
 * but emits `<w:endnoteReference>` and references
 * `EndnoteReference` style instead.
 */
internal class FootnoteReferenceRun(
    val id: Int,
) : XmlComponent("w:r") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:r")
        out.openElement("w:rPr")
        out.selfClosingElement("w:rStyle", "w:val" to "FootnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement("w:footnoteReference", "w:id" to id.toString())
        out.closeElement("w:r")
    }
}

/** Endnote-variant reference run. */
internal class EndnoteReferenceRun(
    val id: Int,
) : XmlComponent("w:r") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:r")
        out.openElement("w:rPr")
        out.selfClosingElement("w:rStyle", "w:val" to "EndnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement("w:endnoteReference", "w:id" to id.toString())
        out.closeElement("w:r")
    }
}

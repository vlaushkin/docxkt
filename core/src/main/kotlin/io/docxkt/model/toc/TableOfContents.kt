// Port of: src/file/table-of-contents/table-of-contents.ts
// (TableOfContents — minimal, no cached entries).
package io.docxkt.model.toc

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.XmlEscape
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:sdt>` block wrapping a two-paragraph TOC complex-field
 * chain. Emits the minimal form (no cached-entry paragraphs);
 * Word generates the entries when the document is opened.
 *
 * Wire (from upstream probe):
 * ```
 * <w:sdt>
 *   <w:sdtPr>[<w:alias w:val="…"/>]</w:sdtPr>
 *   <w:sdtContent>
 *     <w:p><w:r>
 *       <w:fldChar w:fldCharType="begin" w:dirty="true"/>
 *       <w:instrText xml:space="preserve">TOC …</w:instrText>
 *       <w:fldChar w:fldCharType="separate"/>
 *     </w:r></w:p>
 *     <w:p><w:r>
 *       <w:fldChar w:fldCharType="end"/>
 *     </w:r></w:p>
 *   </w:sdtContent>
 * </w:sdt>
 * ```
 *
 * The `w:dirty="true"` attribute on the begin fldChar tells
 * Word/LibreOffice to re-evaluate the field on document open.
 * Upstream's `TableOfContents` passes `beginDirty = true` as
 * its default.
 */
internal class TableOfContents(
    val alias: String? = "Table of Contents",
    val options: TocOptions = TocOptions(),
) : XmlComponent("w:sdt") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:sdt")
        // sdtPr with optional alias.
        out.openElement("w:sdtPr")
        alias?.let { out.selfClosingElement("w:alias", "w:val" to it) }
        out.closeElement("w:sdtPr")
        // sdtContent — two paragraphs.
        out.openElement("w:sdtContent")
        // Begin paragraph.
        out.openElement("w:p")
        out.openElement("w:r")
        out.selfClosingElement(
            "w:fldChar",
            "w:fldCharType" to "begin",
            "w:dirty" to "true",
        )
        out.openElement("w:instrText", "xml:space" to "preserve")
        // Escape " to &quot; in the instruction body — matches
        // upstream's wire (their serializer escapes all
        // attribute-significant chars in text content).
        out.append(XmlEscape.escapeAttributeValue(options.buildInstruction()))
        out.closeElement("w:instrText")
        out.selfClosingElement("w:fldChar", "w:fldCharType" to "separate")
        out.closeElement("w:r")
        out.closeElement("w:p")
        // End paragraph.
        out.openElement("w:p")
        out.openElement("w:r")
        out.selfClosingElement("w:fldChar", "w:fldCharType" to "end")
        out.closeElement("w:r")
        out.closeElement("w:p")
        out.closeElement("w:sdtContent")
        out.closeElement("w:sdt")
    }
}

// Port of: src/file/paragraph/run/field.ts (createBegin/Separate/End) +
// src/file/paragraph/run/page-number.ts (Page, NumberOfPages etc.) +
// src/file/paragraph/run/run.ts §125-215 (PageNumber expansion).
package io.docxkt.model.field

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement
import io.docxkt.xml.textElement

/**
 * The three-marker complex-field chain, wrapped in a single
 * `<w:r>`. Emits:
 *
 * ```
 * <w:r>
 *   <w:fldChar w:fldCharType="begin"/>
 *   <w:instrText xml:space="preserve">INSTR</w:instrText>
 *   <w:fldChar w:fldCharType="separate"/>
 *   <!-- optional cached text -->
 *   <w:fldChar w:fldCharType="end"/>
 * </w:r>
 * ```
 *
 * Matches upstream's `PageNumber.CURRENT` expansion in
 * `Run.constructor` — all four chain parts share a single run.
 * When the cached-result needs distinct formatting, a future
 * extension can split across multiple runs; we keep the
 * single-run form to mirror upstream's common emission.
 */
internal class ComplexField(
    val instruction: String,
    val cached: String? = null,
    /**
     * When true, emit `w:dirty="true"` on the begin fldChar. Tells
     * Word to recalculate the field at open time. Used by SEQ /
     * other dynamic fields where the value changes with document
     * structure.
     */
    val dirty: Boolean = false,
) : XmlComponent("w:r") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:r")
        out.selfClosingElement(
            "w:fldChar",
            "w:fldCharType" to "begin",
            "w:dirty" to if (dirty) "true" else null,
        )
        out.textElement("w:instrText", instruction, "xml:space" to "preserve")
        out.selfClosingElement("w:fldChar", "w:fldCharType" to "separate")
        if (cached != null) {
            out.textElement("w:t", cached, "xml:space" to "preserve")
        }
        out.selfClosingElement("w:fldChar", "w:fldCharType" to "end")
        out.closeElement("w:r")
    }
}

// Port of: src/file/paragraph/properties.ts (ParagraphProperties, L248-L419).
package io.docxkt.model.paragraph

import io.docxkt.model.numbering.NumberingReference
import io.docxkt.model.section.SectionProperties
import io.docxkt.model.shading.Shading
import io.docxkt.model.shading.writeShading
import io.docxkt.xml.IgnoreIfEmptyXmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.onOff
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:pPr>` — paragraph properties.
 *
 * Unset (`null`) fields mean "inherit". Setting any field to a non-null
 * value causes `<w:pPr>` to be emitted; an all-null properties component
 * is skipped entirely via [IgnoreIfEmptyXmlComponent].
 *
 * Child order matches upstream's `properties.ts`:
 * `pStyle → keepNext → keepLines → pageBreakBefore → widowControl →
 * pBdr → shd → tabs → bidi → spacing → ind → contextualSpacing →
 * jc → outlineLvl → suppressLineNumbers`. Upstream also emits
 * numPr and others in the interstices — those are later-phase scope.
 *
 * `pageBreakBefore` uses an OOXML-native shape different from OnOff:
 * upstream emits `<w:pageBreakBefore/>` when true and nothing otherwise
 * — no `w:val="false"` form. We match.
 */
internal class ParagraphProperties(
    val styleReference: String? = null,
    val keepNext: Boolean? = null,
    val keepLines: Boolean? = null,
    val pageBreakBefore: Boolean? = null,
    val framePr: FrameProperties? = null,
    val widowControl: Boolean? = null,
    val numbering: NumberingReference? = null,
    val borders: ParagraphBorders? = null,
    val shading: Shading? = null,
    /**
     * `<w:wordWrap>` — when true, emit `<w:wordWrap w:val="0"/>` to
     * preserve word-wrap (don't break long Latin words). Upstream's
     * `wordWrap: true` quirk: true emits `val=0`, false emits nothing.
     */
    val wordWrap: Boolean? = null,
    val tabStops: TabStops? = null,
    val bidirectional: Boolean? = null,
    val spacing: Spacing? = null,
    val indentation: Indentation? = null,
    val contextualSpacing: Boolean? = null,
    val alignment: AlignmentType? = null,
    val outlineLevel: Int? = null,
    val suppressLineNumbers: Boolean? = null,
    /**
     * Paragraph-level run defaults (`<w:rPr>` inside `<w:pPr>`).
     * Inherited by all runs in this paragraph unless overridden.
     */
    val runDefaults: io.docxkt.model.paragraph.run.RunProperties? = null,
    /**
     * Section break — when non-null, emit `<w:sectPr>` as the LAST
     * child of `<w:pPr>` (mid-body section-end marker). Must remain
     * the final child per OOXML schema.
     */
    val sectionProperties: SectionProperties? = null,
    /**
     * `<w:pPrChange>` revision wrapper. When non-null, emits
     * `<w:pPrChange w:id w:author w:date>` as the LAST child of
     * `<w:pPr>`, carrying [revisionOldProps] inside as a nested
     * `<w:pPr>`. Mirrors upstream's `ParagraphPropertiesChange`.
     */
    val revision: io.docxkt.model.revision.RevisionInfo? = null,
    val revisionOldProps: ParagraphProperties? = null,
) : IgnoreIfEmptyXmlComponent("w:pPr") {

    override fun isEmpty(): Boolean = (
        styleReference == null &&
            keepNext == null && keepLines == null &&
            // pageBreakBefore is truthy-only emission (see writeNonEmpty);
            // false should NOT trip the wrapper, parallel to
            // RunProperties' vanish quirk.
            pageBreakBefore != true &&
            framePr == null &&
            widowControl == null && numbering == null && borders == null &&
            shading == null && wordWrap != true && tabStops == null && bidirectional == null &&
            (spacing == null || spacing.isEmpty()) &&
            (indentation == null || indentation.isEmpty()) &&
            contextualSpacing == null && alignment == null &&
            outlineLevel == null && suppressLineNumbers == null &&
            runDefaults == null &&
            sectionProperties == null &&
            revision == null
    )

    override fun writeNonEmpty(out: Appendable) {
        out.openElement("w:pPr")
        styleReference?.let { out.selfClosingElement("w:pStyle", "w:val" to it) }
        out.onOff("w:keepNext", keepNext)
        out.onOff("w:keepLines", keepLines)
        if (pageBreakBefore == true) out.selfClosingElement("w:pageBreakBefore")
        framePr?.appendXml(out)
        out.onOff("w:widowControl", widowControl)
        numbering?.appendXml(out)
        borders?.appendXml(out)
        shading?.let { writeShading(out, it) }
        if (wordWrap == true) out.selfClosingElement("w:wordWrap", "w:val" to "0")
        tabStops?.appendXml(out)
        out.onOff("w:bidi", bidirectional)
        spacing?.takeUnless { it.isEmpty() }?.let { writeSpacing(out, it) }
        indentation?.takeUnless { it.isEmpty() }?.let { writeIndent(out, it) }
        out.onOff("w:contextualSpacing", contextualSpacing)
        alignment?.let { out.selfClosingElement("w:jc", "w:val" to it.wire) }
        outlineLevel?.let {
            out.selfClosingElement("w:outlineLvl", "w:val" to it.toString())
        }
        out.onOff("w:suppressLineNumbers", suppressLineNumbers)
        runDefaults?.appendXml(out)
        sectionProperties?.appendXml(out)
        // <w:pPrChange> — last child of <w:pPr>.
        revision?.let {
            out.openElement("w:pPrChange", *it.attrs())
            (revisionOldProps ?: ParagraphProperties()).appendXml(out)
            out.closeElement("w:pPrChange")
        }
        out.closeElement("w:pPr")
    }

    private fun writeSpacing(out: Appendable, s: Spacing) {
        out.selfClosingElement(
            "w:spacing",
            "w:after" to s.after?.toString(),
            "w:before" to s.before?.toString(),
            "w:line" to s.line?.toString(),
            "w:lineRule" to s.lineRule?.wire,
            "w:beforeAutospacing" to s.beforeAutoSpacing?.let { if (it) "1" else "0" },
            "w:afterAutospacing" to s.afterAutoSpacing?.let { if (it) "1" else "0" },
        )
    }

    private fun writeIndent(out: Appendable, i: Indentation) {
        out.selfClosingElement(
            "w:ind",
            "w:start" to i.start?.toString(),
            "w:end" to i.end?.toString(),
            "w:left" to i.left?.toString(),
            "w:right" to i.right?.toString(),
            "w:hanging" to i.hanging?.toString(),
            "w:firstLine" to i.firstLine?.toString(),
        )
    }
}

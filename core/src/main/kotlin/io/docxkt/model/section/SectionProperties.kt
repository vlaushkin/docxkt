// Port of: src/file/document/body/section-properties/section-properties.ts
package io.docxkt.model.section

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:sectPr>` — section properties, emitted as the last child of
 * `<w:body>` (or, mid-body, as a `<w:pPr>` child of a section-end
 * paragraph).
 *
 * Children in upstream's canonical order:
 *   `headerReference → footerReference → pgSz → pgMar → pgBorders
 *    → lnNumType → pgNumType → cols → titlePg → docGrid`.
 *
 * Header/footer references are optional and only emitted when
 * non-null. References emit in the order `default → first → even`
 * for headers, then the same for footers (matching upstream's
 * `addHeaderFooterGroup` push order).
 *
 * Upstream emits `<w:pgNumType/>` with no attributes (meaning
 * "defaults: decimal numbers, start at 1") and `<w:docGrid
 * w:linePitch="360"/>` with the default line pitch. Both are
 * parameterless here — later phases can expand if needed.
 *
 * `<w:titlePg/>` is emitted only when [titlePage] is true. The
 * placement BEFORE docGrid matches upstream's
 * `OnOffElement("w:titlePg", titlePage)` push, which lands after
 * `createPageNumberType` (and any column / vAlign) but before
 * `createDocumentGrid` (per the constructor's push order in
 * section-properties.ts).
 */
internal class SectionProperties(
    val pageSize: PageSize = PageSize.a4(PageOrientation.PORTRAIT),
    val pageMargins: PageMargins = PageMargins(),
    /**
     * Header references for this section, in upstream's emission
     * order (default → first → even). Each section can carry its
     * own list, supporting multi-section per-section H/F. Empty
     * list = no header references.
     */
    val headerRefs: List<HeaderFooterRef> = emptyList(),
    /** Footer references for this section. Same shape as [headerRefs]. */
    val footerRefs: List<HeaderFooterRef> = emptyList(),
    /** Optional page borders. Null = no `<w:pgBorders>`. */
    val pageBorders: PageBorders? = null,
    /** Optional line numbering. Null = no `<w:lnNumType>`. */
    val lineNumbering: LineNumbering? = null,
    /** Optional column layout. Null = no `<w:cols>`. */
    val columns: Columns? = null,
    /** Whether to emit `<w:titlePg/>`. */
    val titlePage: Boolean = false,
    /** Section break kind. Null = inherit / `nextPage`. */
    val type: SectionType? = null,
    /** Section vertical alignment. Null = inherit. */
    val verticalAlign: SectionVerticalAlign? = null,
    /**
     * Page-number formatting on `<w:pgNumType>`.
     * `start` → restart numbering at this value. `format` → roman /
     * decimal / etc. `chapStyle` → outline level for chapter prefix.
     * `chapSep` → separator char between chapter and page number.
     * Null fields are omitted from the wire.
     */
    val pageNumbersStart: Int? = null,
    val pageNumbersFormat: PageNumberFormat? = null,
    val pageNumbersSeparator: PageNumberSeparator? = null,
    val pageNumbersChapStyle: Int? = null,
) : XmlComponent("w:sectPr") {

    override fun appendXml(out: Appendable) {
        out.openElement("w:sectPr")
        // Header refs first, then footer refs. Each list is already in the
        // caller-chosen order (default → first → even canonical).
        for (ref in headerRefs) writeRef(out, "w:headerReference", ref.type.wire, ref.rid)
        for (ref in footerRefs) writeRef(out, "w:footerReference", ref.type.wire, ref.rid)
        type?.let { out.selfClosingElement("w:type", "w:val" to it.wire) }
        out.selfClosingElement(
            "w:pgSz",
            "w:w" to pageSize.widthTwips.toString(),
            "w:h" to pageSize.heightTwips.toString(),
            "w:orient" to pageSize.orientation.wire,
        )
        out.selfClosingElement(
            "w:pgMar",
            "w:top" to pageMargins.top.toString(),
            "w:right" to pageMargins.right.toString(),
            "w:bottom" to pageMargins.bottom.toString(),
            "w:left" to pageMargins.left.toString(),
            "w:header" to pageMargins.header.toString(),
            "w:footer" to pageMargins.footer.toString(),
            "w:gutter" to pageMargins.gutter.toString(),
        )
        pageBorders?.appendXml(out)
        lineNumbering?.appendXml(out)
        out.selfClosingElement(
            "w:pgNumType",
            "w:start" to pageNumbersStart?.toString(),
            "w:fmt" to pageNumbersFormat?.wire,
            "w:chapStyle" to pageNumbersChapStyle?.toString(),
            "w:chapSep" to pageNumbersSeparator?.wire,
        )
        columns?.appendXml(out)
        verticalAlign?.let { out.selfClosingElement("w:vAlign", "w:val" to it.wire) }
        if (titlePage) out.selfClosingElement("w:titlePg")
        out.selfClosingElement("w:docGrid", "w:linePitch" to "360")
        out.closeElement("w:sectPr")
    }

    private fun writeRef(out: Appendable, element: String, type: String, rid: String) {
        out.selfClosingElement(element, "w:type" to type, "r:id" to rid)
    }

    companion object {
        /** Upstream's default: A4 portrait + default margins. */
        fun default(): SectionProperties = SectionProperties()
    }
}

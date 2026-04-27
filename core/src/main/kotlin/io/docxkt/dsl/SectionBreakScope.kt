// No upstream analogue — DSL scope for mid-body section breaks
// (`<w:sectPr>` inside a paragraph's `<w:pPr>`).
package io.docxkt.dsl

import io.docxkt.model.section.Column
import io.docxkt.model.section.Columns
import io.docxkt.model.section.LineNumberRestart
import io.docxkt.model.section.LineNumbering
import io.docxkt.model.section.PageBorders
import io.docxkt.model.section.PageMargins
import io.docxkt.model.section.PageOrientation
import io.docxkt.model.section.PageSize
import io.docxkt.model.footer.Footer
import io.docxkt.model.header.Header
import io.docxkt.model.section.HeaderFooterReferenceType
import io.docxkt.model.section.PageNumberFormat
import io.docxkt.model.section.PageNumberSeparator
import io.docxkt.model.section.SectionType
import io.docxkt.model.section.SectionVerticalAlign

/**
 * Builder for the section that ENDS at the position where
 * `DocumentScope.sectionBreak { … }` is called. Carries the
 * page-level properties for that section; the next section
 * inherits the document-level defaults until another
 * sectionBreak (or the document-trailing sectPr) reconfigures.
 *
 * Supports pgSz, pgMar, and pgBorders. Per-section headers /
 * footers are not yet supported — the document-level
 * `header { }` / `footer { }` apply to the trailing section
 * only.
 */
@DocxktDsl
public class SectionBreakScope internal constructor(
    internal val context: DocumentContext = DocumentContext(),
) {
    internal var pageSizeValue: PageSize = PageSize.a4(PageOrientation.PORTRAIT)
    internal var pageMarginsValue: PageMargins = PageMargins()
    internal var pageBordersValue: PageBorders? = null
    internal var lineNumberingValue: LineNumbering? = null
    internal var columnsValue: Columns? = null
    internal var typeValue: SectionType? = null
    internal var verticalAlignValue: SectionVerticalAlign? = null
    internal var pageNumbersStartValue: Int? = null
    internal var pageNumbersFormatValue: PageNumberFormat? = null
    internal var pageNumbersSeparatorValue: PageNumberSeparator? = null
    internal var pageNumbersChapStyleValue: Int? = null
    internal val headers: MutableMap<HeaderFooterReferenceType, Header> = mutableMapOf()
    internal val footers: MutableMap<HeaderFooterReferenceType, Footer> = mutableMapOf()
    internal var titlePageValue: Boolean = false
    internal var evenAndOddHeadersValue: Boolean = false

    /**
     * Configure a header for this section. [type] selects which
     * type the header applies to (default / first / even). Calling
     * twice with the same [type] replaces the previous value.
     *
     * Setting `EVEN` auto-enables `<w:evenAndOddHeaders/>` on the
     * document settings (a global flag). Setting `FIRST` does NOT
     * auto-enable `<w:titlePg/>` — call [titlePage] explicitly to
     * mirror upstream's behaviour.
     */
    public fun header(
        type: HeaderFooterReferenceType = HeaderFooterReferenceType.DEFAULT,
        configure: HeaderScope.() -> Unit,
    ) {
        val scope = HeaderScope(context)
        scope.configure()
        headers[type] = scope.build()
        if (type == HeaderFooterReferenceType.EVEN) evenAndOddHeadersValue = true
    }

    /** Configure a footer for this section. Same per-type rules as [header]. */
    public fun footer(
        type: HeaderFooterReferenceType = HeaderFooterReferenceType.DEFAULT,
        configure: FooterScope.() -> Unit,
    ) {
        val scope = FooterScope(context)
        scope.configure()
        footers[type] = scope.build()
        if (type == HeaderFooterReferenceType.EVEN) evenAndOddHeadersValue = true
    }

    /** Enable `<w:titlePg/>` on this section. */
    public fun titlePage() {
        titlePageValue = true
    }

    /**
     * Configure `<w:pgNumType>` attributes for this section.
     * Any null parameter omits the matching attribute.
     */
    public fun pageNumbers(
        start: Int? = null,
        format: PageNumberFormat? = null,
        separator: PageNumberSeparator? = null,
        chapStyle: Int? = null,
    ) {
        pageNumbersStartValue = start
        pageNumbersFormatValue = format
        pageNumbersSeparatorValue = separator
        pageNumbersChapStyleValue = chapStyle
    }

    /**
     * Section break kind (`<w:type w:val="…"/>`). Default
     * is `nextPage` (omitted from XML). Set this to
     * `CONTINUOUS` for a section break that doesn't force a
     * new page, etc.
     */
    public fun type(value: SectionType) {
        typeValue = value
    }

    /**
     * Section vertical alignment (`<w:vAlign w:val="…"/>`).
     * Controls vertical text positioning of the section's
     * content within the page area.
     */
    public fun verticalAlign(value: SectionVerticalAlign) {
        verticalAlignValue = value
    }

    /** Set the page size explicitly in twips. */
    public fun pageSize(
        widthTwips: Int,
        heightTwips: Int,
        orientation: PageOrientation = PageOrientation.PORTRAIT,
    ) {
        pageSizeValue = PageSize(widthTwips, heightTwips, orientation)
    }

    /** A4 portrait. */
    public fun a4Portrait() {
        pageSizeValue = PageSize.a4(PageOrientation.PORTRAIT)
    }

    /** A4 landscape. */
    public fun a4Landscape() {
        pageSizeValue = PageSize.a4(PageOrientation.LANDSCAPE)
    }

    /** Set page margins in twips. Null falls through to upstream defaults. */
    public fun margins(
        top: Int? = null,
        right: Int? = null,
        bottom: Int? = null,
        left: Int? = null,
        header: Int? = null,
        footer: Int? = null,
        gutter: Int? = null,
    ) {
        val d = PageMargins()
        pageMarginsValue = PageMargins(
            top = top ?: d.top,
            right = right ?: d.right,
            bottom = bottom ?: d.bottom,
            left = left ?: d.left,
            header = header ?: d.header,
            footer = footer ?: d.footer,
            gutter = gutter ?: d.gutter,
        )
    }

    /** Configure `<w:pgBorders>` for this section. */
    public fun pageBorders(configure: PageBordersScope.() -> Unit) {
        val scope = PageBordersScope().apply(configure)
        pageBordersValue = PageBorders(
            top = scope.top,
            left = scope.left,
            bottom = scope.bottom,
            right = scope.right,
            display = scope.display,
            offsetFrom = scope.offsetFrom,
            zOrder = scope.zOrder,
        )
    }

    /** Configure `<w:cols>` for this section. */
    public fun columns(
        count: Int,
        equalWidth: Boolean? = null,
        spaceTwips: Int? = null,
        separator: Boolean? = null,
        individual: List<Column> = emptyList(),
    ) {
        columnsValue = Columns(
            count = count,
            equalWidth = equalWidth,
            spaceTwips = spaceTwips,
            separator = separator,
            individual = individual,
        )
    }

    /** Configure `<w:lnNumType>` for this section. */
    public fun lineNumbering(
        countBy: Int? = null,
        start: Int? = null,
        distanceTwips: Int? = null,
        restart: LineNumberRestart? = null,
    ) {
        lineNumberingValue = LineNumbering(
            countBy = countBy,
            start = start,
            distance = distanceTwips,
            restart = restart,
        )
    }
}

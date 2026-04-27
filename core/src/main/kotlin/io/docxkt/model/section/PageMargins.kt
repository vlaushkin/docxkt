// Port of: src/file/document/body/section-properties/page-margin/page-margin-attributes.ts
package io.docxkt.model.section

import io.docxkt.measure.Twips

/**
 * `<w:pgMar>` — page margins in twips (1 inch = [Twips.PER_INCH] =
 * 1440 twips).
 *
 * Default values match upstream's default section-properties
 * verbatim: top/right/bottom/left = 1 inch, header/footer = **708**
 * (not 720 — upstream uses 708, confirmed by probing a trivial
 * document), gutter = 0.
 */
public data class PageMargins(
    val top: Int = Twips.DEFAULT_PAGE_MARGIN,
    val right: Int = Twips.DEFAULT_PAGE_MARGIN,
    val bottom: Int = Twips.DEFAULT_PAGE_MARGIN,
    val left: Int = Twips.DEFAULT_PAGE_MARGIN,
    val header: Int = Twips.DEFAULT_HEADER_FOOTER_OFFSET,
    val footer: Int = Twips.DEFAULT_HEADER_FOOTER_OFFSET,
    val gutter: Int = 0,
)

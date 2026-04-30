// Port of: src/file/document/body/section-properties/page-size/page-size.ts
package io.docxkt.model.section

/**
 * `<w:pgSz>` — page dimensions in twips plus orientation.
 *
 * Upstream's defaults for A4 are `w=11906 h=16838 orient=portrait`
 * in portrait; landscape swaps the dimensions. Use the [Companion]
 * factories to avoid hardcoding.
 */
public data class PageSize(
    val widthTwips: Int,
    val heightTwips: Int,
    val orientation: PageOrientation,
) {
    public companion object {
        /**
         * A4 paper, the upstream default. `widthTwips = 11906`,
         * `heightTwips = 16838`. In landscape the two are swapped.
         */
        public fun a4(orientation: PageOrientation = PageOrientation.PORTRAIT): PageSize =
            when (orientation) {
                PageOrientation.PORTRAIT -> PageSize(
                    widthTwips = 11906, heightTwips = 16838, orientation = PageOrientation.PORTRAIT,
                )
                PageOrientation.LANDSCAPE -> PageSize(
                    widthTwips = 16838, heightTwips = 11906, orientation = PageOrientation.LANDSCAPE,
                )
            }
    }
}

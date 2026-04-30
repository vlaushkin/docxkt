// No upstream analogue — Kotlin idiom for OOXML measurement
// constants. Upstream uses inline numeric literals
// (1440 / 914400 / 9525) scattered across page-margin defaults,
// drawing dimension converters, and KDoc examples. Gathered
// here as a single source of truth.
package io.docxkt.measure

/**
 * **Twips** — twentieth of a point, OOXML's primary measurement
 * unit for `<w:pgSz>`, `<w:pgMar>`, `<w:ind>`, `<w:spacing>`, and
 * `<w:tblW w:type="dxa">`. 1 inch = 1440 twips, 1 cm ≈ 567 twips,
 * 1 point = 20 twips.
 *
 * Upstream's `dolanmiu/docx` uses `convertInchesToTwip()` etc. for
 * conversions; we expose the underlying constants and let callers
 * compute as needed.
 */
public object Twips {
    /** 1 inch = 1440 twips. The canonical Word page-margin unit. */
    public const val PER_INCH: Int = 1440

    /** 1 cm ≈ 567 twips (rounded down from 566.929…). */
    public const val PER_CM: Int = 567

    /** 1 mm ≈ 56 twips (rounded down from 56.6929…). */
    public const val PER_MM: Int = 56

    /** 1 point = 20 twips. */
    public const val PER_POINT: Int = 20

    /**
     * Word's default 1-inch page margin (top / right / bottom / left).
     * Used by `<w:pgMar>` defaults in [io.docxkt.model.section.PageMargins].
     */
    public const val DEFAULT_PAGE_MARGIN: Int = PER_INCH

    /** Word's default header / footer offset = 708 twips ≈ 0.49 inch. */
    public const val DEFAULT_HEADER_FOOTER_OFFSET: Int = 708

    /** Maximum legal `<w:tabs>` position (`Number.MAX_VALUE` upstream). */
    public const val MAX_TAB_STOP: Int = 9_999_999
}

/**
 * **EMU** (English Metric Unit) — DrawingML's measurement unit for
 * `<wp:extent cx/cy>`, `<a:off>`, `<a:ext>`, etc. 1 inch =
 * 914,400 EMUs; at 96 DPI, 1 pixel = 9,525 EMUs.
 *
 * EMUs are integers chosen so that 1 inch / 1 cm / 1 point all
 * have integer EMU equivalents; the unit is shared with PowerPoint
 * and Excel DrawingML.
 */
public object Emu {
    /** 1 inch = 914,400 EMUs. */
    public const val PER_INCH: Int = 914_400

    /** 1 centimetre = 360,000 EMUs. */
    public const val PER_CM: Int = 360_000

    /** 1 millimetre = 36,000 EMUs. */
    public const val PER_MM: Int = 36_000

    /** 1 point = 12,700 EMUs (= 914_400 / 72). */
    public const val PER_POINT: Int = 12_700

    /** At 96 DPI, 1 CSS pixel = 9,525 EMUs. */
    public const val PER_PX_AT_96_DPI: Int = 9_525
}

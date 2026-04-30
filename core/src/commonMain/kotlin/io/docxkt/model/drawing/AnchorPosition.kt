// Port of: src/file/drawing/floating/horizontal-position.ts +
//          src/file/drawing/floating/vertical-position.ts +
//          src/file/drawing/floating/position-offset.ts +
//          src/file/drawing/floating/align.ts +
//          src/file/drawing/floating/simple-pos.ts
package io.docxkt.model.drawing

/**
 * `<wp:positionH relativeFrom="…">` content. Exactly one of
 * [align] / [offsetEmus] must be set — matches upstream's
 * `createHorizontalPosition` "throw if neither" check.
 */
public data class HorizontalPosition(
    val relativeFrom: HorizontalRelativeFrom,
    val align: HorizontalAlign? = null,
    val offsetEmus: Int? = null,
) {
    init {
        require((align == null) xor (offsetEmus == null)) {
            "HorizontalPosition: provide exactly one of align or offsetEmus"
        }
    }
}

/**
 * `<wp:positionV relativeFrom="…">` content. Same XOR
 * invariant as [HorizontalPosition].
 */
public data class VerticalPosition(
    val relativeFrom: VerticalRelativeFrom,
    val align: VerticalAlign? = null,
    val offsetEmus: Int? = null,
) {
    init {
        require((align == null) xor (offsetEmus == null)) {
            "VerticalPosition: provide exactly one of align or offsetEmus"
        }
    }
}

/**
 * Wrap-distance margins surrounding a floating drawing.
 * Used both as the anchor-level `distT/distB/distL/distR`
 * and as the per-wrap-element variants on
 * `<wp:wrapSquare>` / `<wp:wrapTight>` /
 * `<wp:wrapTopAndBottom>`.
 *
 * Defaults to all-zero — matches upstream's fallback.
 */
public data class AnchorMargins(
    val topEmus: Int = 0,
    val bottomEmus: Int = 0,
    val leftEmus: Int = 0,
    val rightEmus: Int = 0,
)

/**
 * The wrap policy for a floating drawing — how surrounding
 * text flows around or behind the image. Subtypes mirror
 * upstream's four `createWrap*` factories.
 *
 * `Tight` and `TopAndBottom` carry only top/bottom margins
 * (matches upstream's `IWrapTightAttributes` /
 * `IWrapTopAndBottomAttributes` shape — left/right are
 * controlled at the anchor level).
 *
 * `Square` carries the [side] (default `BOTH_SIDES`) and the
 * full four-margin set; upstream emits all four `dist*`
 * attributes on `<wp:wrapSquare>`.
 *
 * `Through` is omitted (upstream supports it only as an alias
 * for `Tight`'s wire) and `wrapPolygon` is omitted (XSD requires
 * it on tight; upstream omits — we match).
 */
public sealed class AnchorWrap {
    public data object None : AnchorWrap()

    public data class Square(
        val side: WrapSide = WrapSide.BOTH_SIDES,
        val margins: AnchorMargins = AnchorMargins(),
    ) : AnchorWrap()

    public data class Tight(
        val marginTopEmus: Int = 0,
        val marginBottomEmus: Int = 0,
    ) : AnchorWrap()

    public data class TopAndBottom(
        val marginTopEmus: Int = 0,
        val marginBottomEmus: Int = 0,
    ) : AnchorWrap()
}

/**
 * `wrapText` attribute on `<wp:wrapSquare>`.
 */
public enum class WrapSide(internal val wire: String) {
    BOTH_SIDES("bothSides"),
    LEFT("left"),
    RIGHT("right"),
    LARGEST("largest"),
}

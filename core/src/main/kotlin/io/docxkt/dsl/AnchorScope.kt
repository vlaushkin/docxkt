// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.drawing.AnchorMargins
import io.docxkt.model.drawing.AnchorWrap
import io.docxkt.model.drawing.HorizontalAlign
import io.docxkt.model.drawing.HorizontalPosition
import io.docxkt.model.drawing.HorizontalRelativeFrom
import io.docxkt.model.drawing.VerticalAlign
import io.docxkt.model.drawing.VerticalPosition
import io.docxkt.model.drawing.VerticalRelativeFrom
import io.docxkt.model.drawing.WrapSide

/**
 * Configure a floating image anchor — wrap policy, position
 * relative-to bases, anchor margins, and behind/locked
 * flags. Position H and V are required (the anchor wire has
 * no sensible default — consumers must commit).
 */
@DocxktDsl
public class AnchorScope internal constructor() {

    private var horizontal: HorizontalPosition? = null
    private var vertical: VerticalPosition? = null

    /** Wrap policy. Default `None`. */
    public var wrap: AnchorWrap = AnchorWrap.None

    // Anchor attribute booleans (behindDoc / allowOverlap /
    // layoutInCell / lockAnchor) are `use="required"` per
    // CT_Anchor (ECMA-376 §20.4.2.3). Every <wp:anchor> MUST
    // carry all four — there is no "inherit / not set" state on
    // the wire. Kept as plain `Boolean`, not `Boolean?`.

    /** Behind-text flag. Default `false` — image floats above body text. */
    public var behindDoc: Boolean = false

    /** Allow other floating objects to overlap. Default `true`. */
    public var allowOverlap: Boolean = true

    /** Layout in the containing table cell when present. Default `true`. */
    public var layoutInCell: Boolean = true

    /** When `true`, anchor's reference point is locked to the paragraph. */
    public var lockAnchor: Boolean = false

    /**
     * Anchor-level wrap distances (`distT/B/L/R` on
     * `<wp:anchor>`). Default all-zero.
     */
    public var anchorMargins: AnchorMargins = AnchorMargins()

    /**
     * z-order proxy. When `null`, [io.docxkt.model.drawing.AnchorDrawing]
     * defaults to the image's heightEmus per upstream's
     * fallback.
     */
    public var relativeHeight: Int? = null

    /**
     * Set horizontal position via offset from a relative
     * base. Calling either `positionH` overload twice replaces.
     */
    public fun positionH(relativeFrom: HorizontalRelativeFrom, offsetEmus: Int) {
        horizontal = HorizontalPosition(
            relativeFrom = relativeFrom,
            offsetEmus = offsetEmus,
        )
    }

    /** Set horizontal position via alignment relative to a base. */
    public fun positionH(relativeFrom: HorizontalRelativeFrom, align: HorizontalAlign) {
        horizontal = HorizontalPosition(
            relativeFrom = relativeFrom,
            align = align,
        )
    }

    /** Set vertical position via offset from a relative base. */
    public fun positionV(relativeFrom: VerticalRelativeFrom, offsetEmus: Int) {
        vertical = VerticalPosition(
            relativeFrom = relativeFrom,
            offsetEmus = offsetEmus,
        )
    }

    /** Set vertical position via alignment relative to a base. */
    public fun positionV(relativeFrom: VerticalRelativeFrom, align: VerticalAlign) {
        vertical = VerticalPosition(
            relativeFrom = relativeFrom,
            align = align,
        )
    }

    /**
     * Convenience: switch to `Square` wrap with the given
     * side and optional per-side wrap margins. Margins
     * default to 0; zero values are omitted from the wire
     * (matches upstream's `BuilderElement` falsy-omit
     * behaviour).
     */
    public fun wrapSquare(
        side: WrapSide = WrapSide.BOTH_SIDES,
        marginTopEmus: Int = 0,
        marginBottomEmus: Int = 0,
        marginLeftEmus: Int = 0,
        marginRightEmus: Int = 0,
    ) {
        wrap = AnchorWrap.Square(
            side = side,
            margins = AnchorMargins(
                topEmus = marginTopEmus,
                bottomEmus = marginBottomEmus,
                leftEmus = marginLeftEmus,
                rightEmus = marginRightEmus,
            ),
        )
    }

    /** Convenience: switch to `Tight` wrap. */
    public fun wrapTight(marginTopEmus: Int = 0, marginBottomEmus: Int = 0) {
        wrap = AnchorWrap.Tight(marginTopEmus, marginBottomEmus)
    }

    /** Convenience: switch to `TopAndBottom` wrap. */
    public fun wrapTopAndBottom(marginTopEmus: Int = 0, marginBottomEmus: Int = 0) {
        wrap = AnchorWrap.TopAndBottom(marginTopEmus, marginBottomEmus)
    }

    /** Convenience: switch to `None` wrap (default). */
    public fun wrapNone() {
        wrap = AnchorWrap.None
    }

    internal fun resolvedHorizontal(): HorizontalPosition =
        horizontal
            ?: error("imageAnchor: positionH(...) is required")

    internal fun resolvedVertical(): VerticalPosition =
        vertical
            ?: error("imageAnchor: positionV(...) is required")
}

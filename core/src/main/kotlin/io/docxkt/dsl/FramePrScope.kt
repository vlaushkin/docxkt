// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.drawing.HorizontalAlign
import io.docxkt.model.drawing.VerticalAlign
import io.docxkt.model.paragraph.FrameAnchor
import io.docxkt.model.paragraph.FrameDropCap
import io.docxkt.model.paragraph.FramePosition
import io.docxkt.model.paragraph.FrameProperties
import io.docxkt.model.paragraph.FrameWrap
import io.docxkt.model.table.HeightRule

/**
 * Configure a `<w:framePr>` for the enclosing paragraph.
 *
 * `widthTwips` and `heightTwips` are required. `hAnchor` /
 * `vAnchor` default to `PAGE` (matching the most common
 * upstream usage). Position must be set via either
 * `positionXy(...)` or `positionAligned(...)`; calling either
 * twice replaces.
 */
@DocxktDsl
public class FramePrScope internal constructor() {

    /** Required: frame width in twips. */
    public var widthTwips: Int = 0

    /** Required: frame height in twips. */
    public var heightTwips: Int = 0

    /** Horizontal anchor reference. Default: `PAGE`. */
    public var hAnchor: FrameAnchor = FrameAnchor.PAGE

    /** Vertical anchor reference. Default: `PAGE`. */
    public var vAnchor: FrameAnchor = FrameAnchor.PAGE

    /** Text-wrap policy. Default: omitted. */
    public var wrap: FrameWrap? = null

    /** Horizontal spacing between frame and surrounding text (twips). */
    public var hSpaceTwips: Int? = null

    /** Vertical spacing between frame and surrounding text (twips). */
    public var vSpaceTwips: Int? = null

    /** Height rule (auto / atLeast / exact). */
    public var hRule: HeightRule? = null

    /** Drop-cap effect. */
    public var dropCap: FrameDropCap? = null

    /** Drop-cap line count. */
    public var lines: Int? = null

    /** Anchor-lock flag — pin the frame to the paragraph. */
    public var anchorLock: Boolean? = null

    private var position: FramePosition? = null

    /** Set XY positioning (twips from anchor base). */
    public fun positionXy(xTwips: Int, yTwips: Int) {
        position = FramePosition.Xy(xTwips, yTwips)
    }

    /** Set alignment-based positioning. */
    public fun positionAligned(xAlign: HorizontalAlign, yAlign: VerticalAlign) {
        position = FramePosition.Aligned(xAlign, yAlign)
    }

    internal fun build(): FrameProperties = FrameProperties(
        widthTwips = widthTwips,
        heightTwips = heightTwips,
        position = position
            ?: error("framePr: positionXy(...) or positionAligned(...) is required"),
        hAnchor = hAnchor,
        vAnchor = vAnchor,
        wrap = wrap,
        hSpaceTwips = hSpaceTwips,
        vSpaceTwips = vSpaceTwips,
        hRule = hRule,
        dropCap = dropCap,
        lines = lines,
        anchorLock = anchorLock,
    )
}

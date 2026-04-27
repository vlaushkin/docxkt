// Port of: src/file/paragraph/frame/frame-properties.ts (FrameProperties,
// IFrameOptions, DropCapType, FrameAnchorType, FrameWrap).
package io.docxkt.model.paragraph

import io.docxkt.model.drawing.HorizontalAlign
import io.docxkt.model.drawing.VerticalAlign
import io.docxkt.model.table.HeightRule
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/** `w:hAnchor` / `w:vAnchor` value. `ST_HAnchor` / `ST_VAnchor`. */
public enum class FrameAnchor(internal val wire: String) {
    MARGIN("margin"),
    PAGE("page"),
    TEXT("text"),
}

/** `w:wrap` value on `<w:framePr>`. `ST_Wrap`. */
public enum class FrameWrap(internal val wire: String) {
    AROUND("around"),
    AUTO("auto"),
    NONE("none"),
    NOT_BESIDE("notBeside"),
    THROUGH("through"),
    TIGHT("tight"),
}

/** `w:dropCap` value on `<w:framePr>`. `ST_DropCap`. */
public enum class FrameDropCap(internal val wire: String) {
    NONE("none"),
    DROP("drop"),
    MARGIN("margin"),
}

/**
 * Discriminated union for frame positioning. Either
 * coordinate-based (`Xy`) — emits `w:x` / `w:y` — or
 * alignment-based (`Aligned`) — emits `w:xAlign` / `w:yAlign`.
 *
 * Mirrors upstream's `IXYFrameOptions` / `IAlignmentFrameOptions`
 * discriminated union via the `type: "absolute" | "alignment"`
 * field.
 */
public sealed class FramePosition {
    public data class Xy(val xTwips: Int, val yTwips: Int) : FramePosition()
    public data class Aligned(
        val xAlign: HorizontalAlign,
        val yAlign: VerticalAlign,
    ) : FramePosition()
}

/**
 * `<w:framePr>` content. Goes inside `<w:pPr>` between
 * `<w:pageBreakBefore>` and `<w:widowControl>` per upstream's
 * `properties.ts` emit order.
 *
 * Attribute order on the element follows upstream's
 * `BuilderElement` declaration order — null values are
 * suppressed via the `selfClosingElement` helper:
 *   anchorLock → dropCap → w → h → x → y → hAnchor →
 *   vAnchor → hSpace → vSpace → hRule → xAlign → yAlign →
 *   lines → wrap.
 *
 * [hAnchor] / [vAnchor] default to `PAGE` per upstream's
 * common-case usage (the spec lists them as optional, but
 * every real fixture sets them).
 */
internal class FrameProperties(
    val widthTwips: Int,
    val heightTwips: Int,
    val position: FramePosition,
    val hAnchor: FrameAnchor = FrameAnchor.PAGE,
    val vAnchor: FrameAnchor = FrameAnchor.PAGE,
    val wrap: FrameWrap? = null,
    val hSpaceTwips: Int? = null,
    val vSpaceTwips: Int? = null,
    val hRule: HeightRule? = null,
    val dropCap: FrameDropCap? = null,
    val lines: Int? = null,
    val anchorLock: Boolean? = null,
) : XmlComponent("w:framePr") {

    override fun appendXml(out: Appendable) {
        val (xVal, yVal, xAlignVal, yAlignVal) = when (val p = position) {
            is FramePosition.Xy -> Quad(p.xTwips.toString(), p.yTwips.toString(), null, null)
            is FramePosition.Aligned -> Quad(null, null, p.xAlign.wire, p.yAlign.wire)
        }
        out.selfClosingElement(
            "w:framePr",
            "w:anchorLock" to anchorLock?.let { if (it) "true" else "false" },
            "w:dropCap" to dropCap?.wire,
            "w:w" to widthTwips.toString(),
            "w:h" to heightTwips.toString(),
            "w:x" to xVal,
            "w:y" to yVal,
            "w:hAnchor" to hAnchor.wire,
            "w:vAnchor" to vAnchor.wire,
            "w:hSpace" to hSpaceTwips?.toString(),
            "w:vSpace" to vSpaceTwips?.toString(),
            "w:hRule" to hRule?.wire,
            "w:xAlign" to xAlignVal,
            "w:yAlign" to yAlignVal,
            "w:lines" to lines?.toString(),
            "w:wrap" to wrap?.wire,
        )
    }

    private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
}

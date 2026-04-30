// Port of: src/file/border/border.ts (BorderStyle, L125-L180).
package io.docxkt.model.border

/**
 * Border stroke style for `<w:... w:val="...">`.
 *
 * Wire tokens match OOXML's `ST_Border` enumeration (camelCase). Enum
 * names follow Kotlin convention (SCREAMING_SNAKE_CASE). The
 * `NIL_STYLE` case carries the wire value `"nil"`; the Kotlin name
 * avoids `NIL` so KMP's @objc bridging (which lowercases enum cases)
 * doesn't collide with Objective-C's reserved `nil` keyword.
 */
public enum class BorderStyle(internal val wire: String) {
    SINGLE("single"),
    DASH_DOT_STROKED("dashDotStroked"),
    DASHED("dashed"),
    DASH_SMALL_GAP("dashSmallGap"),
    DOT_DASH("dotDash"),
    DOT_DOT_DASH("dotDotDash"),
    DOTTED("dotted"),
    DOUBLE("double"),
    DOUBLE_WAVE("doubleWave"),
    INSET("inset"),
    NIL_STYLE("nil"),
    NONE("none"),
    OUTSET("outset"),
    THICK("thick"),
    THICK_THIN_LARGE_GAP("thickThinLargeGap"),
    THICK_THIN_MEDIUM_GAP("thickThinMediumGap"),
    THICK_THIN_SMALL_GAP("thickThinSmallGap"),
    THIN_THICK_LARGE_GAP("thinThickLargeGap"),
    THIN_THICK_MEDIUM_GAP("thinThickMediumGap"),
    THIN_THICK_SMALL_GAP("thinThickSmallGap"),
    THIN_THICK_THIN_LARGE_GAP("thinThickThinLargeGap"),
    THIN_THICK_THIN_MEDIUM_GAP("thinThickThinMediumGap"),
    THIN_THICK_THIN_SMALL_GAP("thinThickThinSmallGap"),
    THREE_D_EMBOSS("threeDEmboss"),
    THREE_D_ENGRAVE("threeDEngrave"),
    TRIPLE("triple"),
    WAVE("wave"),
}

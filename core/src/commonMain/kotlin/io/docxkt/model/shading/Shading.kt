// Port of: src/file/shading/shading.ts (createShading + ShadingType enum).
package io.docxkt.model.shading

import io.docxkt.xml.selfClosingElement

/**
 * Shading pattern value for `<w:shd w:val="...">`.
 *
 * Mirrors upstream's full ShadingType enumeration. The `NIL_PATTERN`
 * case carries the wire value `"nil"`; the Kotlin name avoids `NIL`
 * so KMP's @objc bridging (which lowercases enum cases) doesn't
 * collide with Objective-C's reserved `nil` keyword.
 */
public enum class ShadingPattern(internal val wire: String) {
    /** No pattern; fill color alone shows through. Idiomatic solid-fill uses CLEAR + `fill`. */
    CLEAR("clear"),
    /** Pattern fully covers the background with `color`. */
    SOLID("solid"),
    /** No shading at all. */
    NIL_PATTERN("nil"),
    HORIZONTAL_STRIPE("horzStripe"),
    VERTICAL_STRIPE("vertStripe"),
    REVERSE_DIAG_STRIPE("reverseDiagStripe"),
    DIAG_STRIPE("diagStripe"),
    HORIZONTAL_CROSS("horzCross"),
    DIAG_CROSS("diagCross"),
    THIN_HORIZONTAL_STRIPE("thinHorzStripe"),
    THIN_VERTICAL_STRIPE("thinVertStripe"),
    THIN_REVERSE_DIAG_STRIPE("thinReverseDiagStripe"),
    THIN_DIAG_STRIPE("thinDiagStripe"),
    THIN_HORIZONTAL_CROSS("thinHorzCross"),
    THIN_DIAG_CROSS("thinDiagCross"),
    PCT_5("pct5"),
    PCT_10("pct10"),
    PCT_12("pct12"),
    PCT_15("pct15"),
    PCT_20("pct20"),
    PCT_25("pct25"),
    PCT_30("pct30"),
    PCT_35("pct35"),
    PCT_37("pct37"),
    PCT_40("pct40"),
    PCT_45("pct45"),
    PCT_50("pct50"),
    PCT_55("pct55"),
    PCT_60("pct60"),
    PCT_62("pct62"),
    PCT_65("pct65"),
    PCT_70("pct70"),
    PCT_75("pct75"),
    PCT_80("pct80"),
    PCT_85("pct85"),
    PCT_87("pct87"),
    PCT_90("pct90"),
    PCT_95("pct95"),
}

/**
 * `<w:shd>` value: pattern + optional pattern foreground color +
 * optional background fill color.
 *
 * - [pattern] — required by the schema.
 * - [color] — pattern foreground in hex RGB or `"auto"`. `null` skips
 *   the attribute entirely.
 * - [fill] — background fill in hex RGB or `"auto"`. `null` skips.
 *
 * The solid-color-cell idiom is `Shading(CLEAR, color = "auto", fill =
 * "EEEEEE")` — the cell shows the fill color with no overlayed pattern.
 */
public data class Shading(
    val pattern: ShadingPattern,
    val color: String? = null,
    val fill: String? = null,
)

/**
 * Emit `<w:shd w:fill="..." w:color="..." w:val="..."/>`.
 *
 * Attribute order `fill, color, val` matches upstream's
 * `BuilderElement` for `createShading`. Note that `val` (the required
 * attribute) is **last** — non-obvious and the reason this helper
 * exists instead of inlining the attribute list at call sites.
 */
internal fun writeShading(out: Appendable, shading: Shading) {
    out.selfClosingElement(
        "w:shd",
        "w:fill" to shading.fill,
        "w:color" to shading.color,
        "w:val" to shading.pattern.wire,
    )
}

// Port of: src/file/border/border.ts (IBorderOptions + createBorderElement).
package io.docxkt.model.border

import io.docxkt.xml.selfClosingElement

/**
 * One side of a border container (`<w:top>`, `<w:bottom>`, `<w:insideH>`,
 * etc.).
 *
 * - [style] — OOXML stroke style, required by the schema.
 * - [size] — border width in **eighth-points**. `8` = 1 pt. An OOXML
 *   quirk — do not multiply at the DSL layer.
 * - [color] — hex RGB (`"FF0000"`) or the literal `"auto"`.
 * - [space] — spacing offset in twips (not points — contradicts the
 *   XSD annotation; mirrors what upstream actually emits). `null` skips
 *   the attribute.
 *
 * Each optional attribute is omitted from the wire when `null`, matching
 * upstream's `createBorderElement` behaviour via `BuilderElement`.
 */
public data class BorderSide(
    val style: BorderStyle = BorderStyle.SINGLE,
    val size: Int? = 4,
    val color: String? = "auto",
    val space: Int? = null,
) {
    public companion object {
        /**
         * Upstream's `DEFAULT_BORDER` for `<w:tblBorders>`'s fill-in:
         * SINGLE stroke, size 4 eighth-points (= 0.5 pt), auto color, no
         * spacing. Used by [io.docxkt.model.table.TableBorders] to fill
         * in sides the caller didn't set.
         */
        public val UPSTREAM_DEFAULT: BorderSide = BorderSide(
            style = BorderStyle.SINGLE,
            size = 4,
            color = "auto",
            space = null,
        )
    }
}

/**
 * Emit a single side element like `<w:top w:val="single" w:color="auto"
 * w:sz="4"/>`. Attribute order `val, color, sz, space` matches upstream's
 * `BuilderElement` definition for `createBorderElement` — emission is
 * byte-stable.
 */
internal fun writeBorderSide(out: Appendable, elementName: String, side: BorderSide) {
    out.selfClosingElement(
        elementName,
        "w:val" to side.style.wire,
        "w:color" to side.color,
        "w:sz" to side.size?.toString(),
        "w:space" to side.space?.toString(),
    )
}

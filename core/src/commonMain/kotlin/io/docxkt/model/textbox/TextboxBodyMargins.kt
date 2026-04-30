// Port of: src/file/drawing/inline/graphic/graphic-data/wps/body-properties.ts
// (margins option fields lIns/rIns/tIns/bIns).
package io.docxkt.model.textbox

/**
 * Per-textbox body margins (inset values), all in EMU. Each
 * value is optional — a `null` field means the corresponding
 * `<wps:bodyPr>` attribute is omitted entirely (upstream's
 * default behaviour).
 */
public data class TextboxBodyMargins(
    val leftEmus: Int? = null,
    val rightEmus: Int? = null,
    val topEmus: Int? = null,
    val bottomEmus: Int? = null,
) {
    internal val isEmpty: Boolean
        get() = leftEmus == null && rightEmus == null &&
            topEmus == null && bottomEmus == null
}

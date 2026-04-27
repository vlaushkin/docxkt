// Port of: src/file/paragraph/formatting/alignment.ts (AlignmentType enum).
package io.docxkt.model.paragraph

/**
 * **Paragraph** justification values for `<w:jc w:val="...">`.
 *
 * For drawing-anchor horizontal alignment (a different concept on
 * the wire), see [io.docxkt.model.drawing.HorizontalAlign].
 *
 * Upstream exposes two aliases that share a wire value: `BOTH` and
 * `JUSTIFIED` both emit `"both"`. We preserve both entry points for
 * source-level parity with upstream's `AlignmentType` object, even
 * though they produce identical OOXML.
 */
public enum class AlignmentType(internal val wire: String) {
    START("start"),
    CENTER("center"),
    END("end"),
    BOTH("both"),
    MEDIUM_KASHIDA("mediumKashida"),
    DISTRIBUTE("distribute"),
    NUM_TAB("numTab"),
    HIGH_KASHIDA("highKashida"),
    LOW_KASHIDA("lowKashida"),
    THAI_DISTRIBUTE("thaiDistribute"),
    LEFT("left"),
    RIGHT("right"),
    JUSTIFIED("both"),
}

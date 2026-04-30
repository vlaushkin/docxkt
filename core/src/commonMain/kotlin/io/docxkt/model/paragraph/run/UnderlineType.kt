// Port of: src/file/paragraph/run/underline.ts (UnderlineType enum, L38-L78).
package io.docxkt.model.paragraph.run

/**
 * Underline style values for `<w:u w:val="...">`.
 *
 * Enum names follow Kotlin convention (SCREAMING_SNAKE_CASE); the wire
 * value mapped to each is the OOXML `ST_Underline` enumeration token
 * (camelCase).
 */
public enum class UnderlineType(internal val wire: String) {
    SINGLE("single"),
    WORDS("words"),
    DOUBLE("double"),
    THICK("thick"),
    DOTTED("dotted"),
    DOTTED_HEAVY("dottedHeavy"),
    DASH("dash"),
    DASHED_HEAVY("dashedHeavy"),
    DASH_LONG("dashLong"),
    DASH_LONG_HEAVY("dashLongHeavy"),
    DOT_DASH("dotDash"),
    DASH_DOT_HEAVY("dashDotHeavy"),
    DOT_DOT_DASH("dotDotDash"),
    DASH_DOT_DOT_HEAVY("dashDotDotHeavy"),
    WAVE("wave"),
    WAVY_HEAVY("wavyHeavy"),
    WAVY_DOUBLE("wavyDouble"),
    NONE("none"),
}

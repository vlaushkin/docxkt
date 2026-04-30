// Port of: src/file/numbering/level.ts (LevelFormat enum, subset).
package io.docxkt.model.numbering

/**
 * `<w:numFmt w:val="...">` — list-level number format.
 *
 * Ships the common subset upstream exposes. The full `ST_NumberFormat`
 * enumeration has 50+ values (Japanese counting, Korean ganada, chart
 * numerals, …); adding more is a one-line change when a consumer asks.
 */
public enum class LevelFormat(internal val wire: String) {
    DECIMAL("decimal"),
    BULLET("bullet"),
    UPPER_ROMAN("upperRoman"),
    LOWER_ROMAN("lowerRoman"),
    UPPER_LETTER("upperLetter"),
    LOWER_LETTER("lowerLetter"),
    NONE("none"),
    ORDINAL("ordinal"),
    CARDINAL_TEXT("cardinalText"),
    ORDINAL_TEXT("ordinalText"),
    HEX("hex"),
    CHICAGO("chicago"),
    DECIMAL_ZERO("decimalZero"),
}

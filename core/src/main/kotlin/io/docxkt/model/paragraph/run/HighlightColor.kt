// Port of: src/file/paragraph/run/properties.ts (HighlightColor enum, L113-L148).
package io.docxkt.model.paragraph.run

/**
 * Highlight color values for `<w:highlight w:val="...">`.
 *
 * Closed set of OOXML `ST_HighlightColor` tokens. Enum names follow
 * Kotlin convention; wire value is the camelCase OOXML token.
 */
public enum class HighlightColor(internal val wire: String) {
    BLACK("black"),
    BLUE("blue"),
    CYAN("cyan"),
    DARK_BLUE("darkBlue"),
    DARK_CYAN("darkCyan"),
    DARK_GRAY("darkGray"),
    DARK_GREEN("darkGreen"),
    DARK_MAGENTA("darkMagenta"),
    DARK_RED("darkRed"),
    DARK_YELLOW("darkYellow"),
    GREEN("green"),
    LIGHT_GRAY("lightGray"),
    MAGENTA("magenta"),
    NONE("none"),
    RED("red"),
    WHITE("white"),
    YELLOW("yellow"),
}

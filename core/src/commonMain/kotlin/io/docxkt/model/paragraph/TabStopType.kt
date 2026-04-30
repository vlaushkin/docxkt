// Port of: src/file/paragraph/formatting/tab-stop.ts (TabStopType enum).
package io.docxkt.model.paragraph

/**
 * Tab-stop justification for `<w:tab w:val="...">`. The wire value is the
 * OOXML `ST_TabJc` token (camelCase / lowercase).
 */
public enum class TabStopType(internal val wire: String) {
    LEFT("left"),
    RIGHT("right"),
    CENTER("center"),
    BAR("bar"),
    CLEAR("clear"),
    DECIMAL("decimal"),
    END("end"),
    NUM("num"),
    START("start"),
}

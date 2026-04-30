// Port of: src/file/paragraph/run/properties.ts (TextEffect enum, L64-L79).
package io.docxkt.model.paragraph.run

/**
 * Text animation effect values for `<w:effect w:val="...">`.
 *
 * These are legacy Word animation effects; modern Word does not
 * actually animate them, but the elements are still valid OOXML.
 * Upstream's full set ships here for source parity.
 */
public enum class TextEffect(internal val wire: String) {
    BLINK_BACKGROUND("blinkBackground"),
    LIGHTS("lights"),
    ANTS_BLACK("antsBlack"),
    ANTS_RED("antsRed"),
    SHIMMER("shimmer"),
    SPARKLE("sparkle"),
    NONE("none"),
}

// Port of: src/file/paragraph/run/positional-tab.ts.
package io.docxkt.model.paragraph.run

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:ptab>` — absolute-position tab stop, typically used in
 * bidirectional text. Unlike `<w:tab/>`, ptabs specify exact
 * alignment and relative-to anchor.
 */
internal class PositionalTab(
    val alignment: PositionalTabAlignment,
    val relativeTo: PositionalTabRelativeTo,
    val leader: PositionalTabLeader,
) : XmlComponent("w:ptab") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:ptab",
            "w:alignment" to alignment.wire,
            "w:relativeTo" to relativeTo.wire,
            "w:leader" to leader.wire,
        )
    }
}

/** `<w:ptab w:alignment>` — text alignment at the tab stop. */
public enum class PositionalTabAlignment(internal val wire: String) {
    LEFT("left"),
    CENTER("center"),
    RIGHT("right"),
}

/** `<w:ptab w:relativeTo>` — what the tab position is relative to. */
public enum class PositionalTabRelativeTo(internal val wire: String) {
    MARGIN("margin"),
    INDENT("indent"),
}

/** `<w:ptab w:leader>` — fill character before the tab. */
public enum class PositionalTabLeader(internal val wire: String) {
    NONE("none"),
    DOT("dot"),
    HYPHEN("hyphen"),
    UNDERSCORE("underscore"),
    MIDDLE_DOT("middleDot"),
}

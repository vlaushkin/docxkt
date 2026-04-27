// Port of: src/file/paragraph/formatting/tab-stop.ts (LeaderType enum).
package io.docxkt.model.paragraph

/**
 * Leader character for `<w:tab w:leader="...">` — the fill character
 * drawn before a tab stop (`"...."`, `"----"`, etc.). Upstream's
 * `LeaderType` shipping set.
 */
public enum class TabLeader(internal val wire: String) {
    DOT("dot"),
    HYPHEN("hyphen"),
    MIDDLE_DOT("middleDot"),
    NONE("none"),
    UNDERSCORE("underscore"),
}

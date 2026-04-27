// Port of: src/file/document/body/section-properties/properties/line-number.ts
package io.docxkt.model.section

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:lnNumType w:restart="…"/>` value — when line numbering
 * resets to its starting value.
 */
public enum class LineNumberRestart(internal val wire: String) {
    /** Continue from the previous section's line numbering. */
    CONTINUOUS("continuous"),

    /** Restart line numbering at the top of each page. */
    NEW_PAGE("newPage"),

    /** Restart line numbering at the start of each section. */
    NEW_SECTION("newSection"),
}

/**
 * `<w:lnNumType>` — line numbering for a section.
 *
 * - [countBy] — display only multiples of this value
 *   (e.g. `5` shows every 5th line).
 * - [start] — starting value when the count restarts.
 * - [distance] — twips between text margin and line-number gutter.
 * - [restart] — when to reset numbering.
 *
 * All four attributes are optional. Pass-through; we don't
 * validate `start ≥ 1` or other range constraints.
 */
internal class LineNumbering(
    val countBy: Int? = null,
    val start: Int? = null,
    val distance: Int? = null,
    val restart: LineNumberRestart? = null,
) : XmlComponent("w:lnNumType") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:lnNumType",
            "w:countBy" to countBy?.toString(),
            "w:start" to start?.toString(),
            "w:restart" to restart?.wire,
            "w:distance" to distance?.toString(),
        )
    }
}

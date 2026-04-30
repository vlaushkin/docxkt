// Port of: src/file/paragraph/formatting/tab-stop.ts (TabStopDefinition,
// createTabStopItem, createTabStop).
package io.docxkt.model.paragraph

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * One `<w:tab>` entry inside `<w:tabs>`.
 *
 * Attribute order in the wire: `val, pos, leader` — matches
 * upstream's `createTabStopItem` BuilderElement.
 */
/**
 * Predefined tab-stop positions matching upstream's `TabStopPosition`.
 */
public object TabStopPosition {
    /** The right-margin tab-stop position in twips. */
    public const val MAX: Int = 9026
}

public data class TabStop(
    val type: TabStopType,
    /**
     * Position in twips. `Number` so callers can pass `Int` for the
     * common case OR `Double` for fractional positions (upstream's
     * `TabStopPosition.MAX / 4 * 3` produces 6769.5; matched literally).
     * Whole-number Doubles render without a decimal point — e.g.
     * `2000.0` emits as `2000`.
     */
    val position: Number,
    val leader: TabLeader? = null,
)

/**
 * `<w:tabs>` container. Empty-list = don't emit; callers should pass
 * `null` rather than an empty list to skip emission.
 */
internal class TabStops(
    val tabs: List<TabStop>,
) : XmlComponent("w:tabs") {

    init {
        require(tabs.isNotEmpty()) { "TabStops must carry at least one tab" }
    }

    override fun appendXml(out: Appendable) {
        out.openElement(elementName)
        for (t in tabs) {
            out.selfClosingElement(
                "w:tab",
                "w:val" to t.type.wire,
                "w:pos" to formatPosition(t.position),
                "w:leader" to t.leader?.wire,
            )
        }
        out.closeElement(elementName)
    }

    private fun formatPosition(value: Number): String {
        // Upstream emits fractional positions verbatim (e.g. "6769.5")
        // and integer positions without a trailing ".0".
        return when (value) {
            is Int, is Long, is Short, is Byte -> value.toString()
            else -> {
                val d = value.toDouble()
                if (d == d.toLong().toDouble()) d.toLong().toString()
                else d.toString()
            }
        }
    }
}

// Port of: src/file/numbering/level.ts (Level class).
package io.docxkt.model.numbering

import io.docxkt.model.paragraph.AlignmentType
import io.docxkt.model.paragraph.Indentation
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * One `<w:lvl>` inside `<w:abstractNum>`.
 *
 * Child order (mandatory): `<w:start>` → `<w:numFmt>` →
 * `<w:lvlText>` → `<w:lvlJc>` → `<w:pPr>` (the latter with
 * `<w:ind>`). Upstream emits `w15:tentative="1"` on every `<w:lvl>`
 * unconditionally — we match for byte-stable fixtures.
 */
internal class NumberingLevel(
    val level: Int,
    val format: LevelFormat,
    val text: String,
    val start: Int = 1,
    val justification: AlignmentType = AlignmentType.LEFT,
    val indentation: Indentation? = null,
    val runProperties: io.docxkt.model.paragraph.run.RunProperties? = null,
) : XmlComponent("w:lvl") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:lvl",
            "w:ilvl" to level.toString(),
            "w15:tentative" to "1",
        )
        out.selfClosingElement("w:start", "w:val" to start.toString())
        out.selfClosingElement("w:numFmt", "w:val" to format.wire)
        out.selfClosingElement("w:lvlText", "w:val" to text)
        out.selfClosingElement("w:lvlJc", "w:val" to justification.wire)
        indentation?.takeUnless { it.isEmpty() }?.let { writeIndentPpr(out, it) }
        runProperties?.appendXml(out)
        out.closeElement("w:lvl")
    }

    private fun writeIndentPpr(out: Appendable, ind: Indentation) {
        out.openElement("w:pPr")
        out.selfClosingElement(
            "w:ind",
            "w:start" to ind.start?.toString(),
            "w:end" to ind.end?.toString(),
            "w:left" to ind.left?.toString(),
            "w:right" to ind.right?.toString(),
            "w:hanging" to ind.hanging?.toString(),
            "w:firstLine" to ind.firstLine?.toString(),
        )
        out.closeElement("w:pPr")
    }
}

// Port of: src/file/paragraph/run/comment-run.ts — CommentRangeStart,
// CommentRangeEnd, CommentReference.
package io.docxkt.model.comment

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:commentRangeStart w:id="…"/>` — opens a commented range
 * inside a paragraph. Paired with a later [CommentRangeEnd]
 * carrying the same numeric id.
 */
internal class CommentRangeStart(
    val id: Int,
) : XmlComponent("w:commentRangeStart") {
    override fun appendXml(out: Appendable) {
        out.selfClosingElement("w:commentRangeStart", "w:id" to id.toString())
    }
}

/** `<w:commentRangeEnd w:id="…"/>`. Closes a commented range. */
internal class CommentRangeEnd(
    val id: Int,
) : XmlComponent("w:commentRangeEnd") {
    override fun appendXml(out: Appendable) {
        out.selfClosingElement("w:commentRangeEnd", "w:id" to id.toString())
    }
}

/**
 * `<w:r><w:commentReference w:id="…"/></w:r>` — the reference
 * marker run typically placed immediately after a
 * [CommentRangeEnd]. Upstream does NOT apply a character style
 * to this run; Word renders via its built-in convention.
 *
 * [properties] is optional: when non-null, the run carries an
 * `<w:rPr>` with that formatting (matches upstream's
 * `new TextRun({ children: [new CommentReference(id)], bold: true })`
 * pattern from demo-73).
 */
internal class CommentReferenceRun(
    val id: Int,
    val properties: io.docxkt.model.paragraph.run.RunProperties? = null,
) : XmlComponent("w:r") {
    override fun appendXml(out: Appendable) {
        out.openElement("w:r")
        properties?.appendXml(out)
        out.selfClosingElement("w:commentReference", "w:id" to id.toString())
        out.closeElement("w:r")
    }
}

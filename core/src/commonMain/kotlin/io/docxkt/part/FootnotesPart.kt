// Port of: src/file/footnotes/footnotes.ts (FootNotes).
package io.docxkt.part

import io.docxkt.model.footnote.Footnote
import io.docxkt.model.footnote.FootnoteType
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/footnotes.xml` — the footnotes collection.
 *
 * Always emits the two mandatory default entries
 * (separator = id -1, continuationSeparator = id 0) before
 * user-defined footnotes. Upstream's `FootNotes` constructor
 * pushes them into `this.root` unconditionally; we do the same.
 *
 * For every user-defined footnote, we prepend a footnoteRef run
 * to the first paragraph — matches upstream's
 * `Footnote.constructor`'s `addRunToFront(new FootnoteRefRun())`
 * call.
 */
internal class FootnotesPart(
    val userFootnotes: List<Footnote>,
) {
    val path: String = "word/footnotes.xml"

    val isNonEmpty: Boolean get() = userFootnotes.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            addAll(Namespaces.FOOTER_ROOT_NAMESPACES)
            add("mc:Ignorable" to Namespaces.DOCUMENT_MC_IGNORABLE)
        }.toTypedArray()
        out.openElement("w:footnotes", *attrs)

        // Mandatory defaults.
        emitSeparator(out, id = -1, markerTag = "w:separator",
                      type = FootnoteType.SEPARATOR)
        emitSeparator(out, id = 0, markerTag = "w:continuationSeparator",
                      type = FootnoteType.CONTINUATION_SEPARATOR)

        // User entries.
        for (note in userFootnotes) {
            emitUserFootnote(out, note)
        }

        out.closeElement("w:footnotes")
    }

    /**
     * Emit a separator / continuationSeparator entry with the
     * upstream-canonical shape:
     *   <w:footnote w:type="…" w:id="…">
     *     <w:p>
     *       <w:pPr><w:spacing w:after="0" w:line="240" w:lineRule="auto"/></w:pPr>
     *       <w:r><w:rPr><w:rStyle w:val="FootnoteReference"/></w:rPr>
     *            <w:footnoteRef/></w:r>
     *       <w:r><markerTag/></w:r>
     *     </w:p>
     *   </w:footnote>
     */
    private fun emitSeparator(
        out: Appendable, id: Int, markerTag: String, type: FootnoteType,
    ) {
        out.openElement(
            "w:footnote",
            "w:type" to type.wire!!,
            "w:id" to id.toString(),
        )
        out.openElement("w:p")
        out.openElement("w:pPr")
        out.selfClosingElement(
            "w:spacing",
            "w:after" to "0",
            "w:line" to "240",
            "w:lineRule" to "auto",
        )
        out.closeElement("w:pPr")
        out.openElement("w:r")
        out.openElement("w:rPr")
        out.selfClosingElement("w:rStyle", "w:val" to "FootnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement("w:footnoteRef")
        out.closeElement("w:r")
        out.openElement("w:r")
        out.selfClosingElement(markerTag)
        out.closeElement("w:r")
        out.closeElement("w:p")
        out.closeElement("w:footnote")
    }

    private fun emitUserFootnote(out: Appendable, note: Footnote) {
        out.openElement("w:footnote", "w:id" to note.id.toString())
        // Emit first paragraph with a prepended footnoteRef run,
        // subsequent paragraphs as-is.
        note.paragraphs.forEachIndexed { idx, para ->
            if (idx == 0) {
                emitParagraphWithRefPrepend(out, para, refTag = "w:footnoteRef")
            } else {
                para.appendXml(out)
            }
        }
        out.closeElement("w:footnote")
    }

    /**
     * Emit a paragraph whose first run is a prepended
     * `<w:r><w:rPr><w:rStyle val="FootnoteReference"/></w:rPr>
     * <w:footnoteRef/></w:r>`.
     */
    private fun emitParagraphWithRefPrepend(
        out: Appendable, para: io.docxkt.model.paragraph.Paragraph, refTag: String,
    ) {
        out.openElement("w:p")
        para.properties?.appendXml(out)
        // Prepend the reference-marker run.
        out.openElement("w:r")
        out.openElement("w:rPr")
        out.selfClosingElement("w:rStyle", "w:val" to "FootnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement(refTag)
        out.closeElement("w:r")
        // Emit the original children.
        for (child in para.children) child.appendXml(out)
        out.closeElement("w:p")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

// Port of: src/file/endnotes/endnotes.ts (Endnotes). Mirrors
// FootnotesPart with w:endnote* tag names.
package io.docxkt.part

import io.docxkt.model.footnote.Footnote
import io.docxkt.model.footnote.FootnoteType
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `word/endnotes.xml` — identical in structure to footnotes
 * but with `<w:endnote>` / `<w:endnoteRef/>` /
 * `<w:continuationSeparator/>` tag names.
 *
 * The internal marker tags match upstream: endnotes still use
 * `<w:separator/>` and `<w:continuationSeparator/>` — it's only
 * the wrapper tag that differs.
 */
internal class EndnotesPart(
    val userEndnotes: List<Footnote>,
) {
    val path: String = "word/endnotes.xml"

    val isNonEmpty: Boolean get() = userEndnotes.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            addAll(Namespaces.FOOTER_ROOT_NAMESPACES)
            add("mc:Ignorable" to Namespaces.DOCUMENT_MC_IGNORABLE)
        }.toTypedArray()
        out.openElement("w:endnotes", *attrs)

        emitSeparator(out, id = -1, markerTag = "w:separator",
                      type = FootnoteType.SEPARATOR)
        emitSeparator(out, id = 0, markerTag = "w:continuationSeparator",
                      type = FootnoteType.CONTINUATION_SEPARATOR)

        for (note in userEndnotes) {
            emitUserEndnote(out, note)
        }

        out.closeElement("w:endnotes")
    }

    private fun emitSeparator(
        out: Appendable, id: Int, markerTag: String, type: FootnoteType,
    ) {
        out.openElement(
            "w:endnote",
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
        out.selfClosingElement("w:rStyle", "w:val" to "EndnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement("w:endnoteRef")
        out.closeElement("w:r")
        out.openElement("w:r")
        out.selfClosingElement(markerTag)
        out.closeElement("w:r")
        out.closeElement("w:p")
        out.closeElement("w:endnote")
    }

    private fun emitUserEndnote(out: Appendable, note: Footnote) {
        out.openElement("w:endnote", "w:id" to note.id.toString())
        note.paragraphs.forEachIndexed { idx, para ->
            if (idx == 0) {
                emitParagraphWithRefPrepend(out, para, refTag = "w:endnoteRef")
            } else {
                para.appendXml(out)
            }
        }
        out.closeElement("w:endnote")
    }

    private fun emitParagraphWithRefPrepend(
        out: Appendable, para: io.docxkt.model.paragraph.Paragraph, refTag: String,
    ) {
        out.openElement("w:p")
        para.properties?.appendXml(out)
        out.openElement("w:r")
        out.openElement("w:rPr")
        out.selfClosingElement("w:rStyle", "w:val" to "EndnoteReference")
        out.closeElement("w:rPr")
        out.selfClosingElement(refTag)
        out.closeElement("w:r")
        for (child in para.children) child.appendXml(out)
        out.closeElement("w:p")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

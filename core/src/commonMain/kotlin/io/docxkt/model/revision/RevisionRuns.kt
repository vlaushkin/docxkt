// Port of: src/file/track-revision/track-revision-components/
// {inserted-text-run, deleted-text-run}.ts.
package io.docxkt.model.revision

import io.docxkt.model.paragraph.run.Run
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.textElement

/**
 * `<w:ins w:id="N" w:author="…" w:date="…">` wrapping a list
 * of regular `<w:r>` runs. Attribute order: `w:id → w:author →
 * w:date` (matches upstream's `ChangeAttributes.xmlKeys`).
 *
 * Inner runs are emitted as-is — their rPr, text, and breaks
 * pass through unchanged. This is the "text added" side of
 * track-changes; the content is a normal document run.
 */
internal class InsertedRun(
    val id: Int,
    val author: String,
    val date: String,
    val runs: List<Run>,
) : XmlComponent("w:ins") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:ins",
            "w:id" to id.toString(),
            "w:author" to author,
            "w:date" to date,
        )
        for (r in runs) r.appendXml(out)
        out.closeElement("w:ins")
    }
}

/**
 * `<w:del w:id w:author w:date>` wrapping runs where each
 * `<w:t>` child is rewritten to `<w:delText>`. The inner
 * rewriting happens at construction time — callers hand a
 * regular list of `Run`s and we swap `Text` for `DeletedText`.
 *
 * Non-Text run children (breaks, fields, etc.) pass through
 * unchanged — upstream's `DeletedTextRunWrapper` also
 * preserves them.
 */
internal class DeletedRun(
    val id: Int,
    val author: String,
    val date: String,
    val runs: List<Run>,
) : XmlComponent("w:del") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:del",
            "w:id" to id.toString(),
            "w:author" to author,
            "w:date" to date,
        )
        for (r in runs) emitRunWithDelText(out, r)
        out.closeElement("w:del")
    }

    private fun emitRunWithDelText(out: Appendable, run: Run) {
        // Rewrite Text → DeletedText and InstrText → DeletedInstrText.
        // Other children (FieldChar, breaks) pass through.
        val rewritten = Run(
            children = run.children.map { child ->
                when (child) {
                    is io.docxkt.model.paragraph.run.Text -> DeletedText(value = child.value)
                    is io.docxkt.model.field.InstrText -> DeletedInstrText(instruction = child.instruction)
                    else -> child
                }
            },
            properties = run.properties,
        )
        rewritten.appendXml(out)
    }
}

/**
 * `<w:delInstrText xml:space="preserve">CODE</w:delInstrText>` —
 * the deleted-side mirror of `<w:instrText>`. Used inside a
 * `<w:del>` wrapper when the deletion contains a complex field
 * like PageNumber/NUMPAGES.
 */
internal class DeletedInstrText(
    val instruction: String,
) : XmlComponent("w:delInstrText") {

    override fun appendXml(out: Appendable) {
        out.textElement("w:delInstrText", instruction, "xml:space" to "preserve")
    }
}

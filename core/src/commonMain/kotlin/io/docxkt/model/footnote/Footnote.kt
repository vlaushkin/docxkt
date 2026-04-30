// Port of: src/file/footnotes/footnote/footnote.ts (Footnote).
package io.docxkt.model.footnote

import io.docxkt.model.paragraph.Paragraph

/**
 * A single footnote or endnote body — the value passed into
 * `DocumentScope.footnote(id) { … }` / `endnote(id) { … }`.
 *
 * Emission is handled by [io.docxkt.part.FootnotesPart] /
 * [io.docxkt.part.EndnotesPart] — they wrap these values in
 * `<w:footnote>` / `<w:endnote>` and auto-prepend the
 * `FootnoteRef` / `EndnoteRef` marker to the first paragraph.
 *
 * Not an `XmlComponent` because the wrapping element name
 * differs between footnote and endnote parts.
 */
internal data class Footnote(
    val id: Int,
    val type: FootnoteType = FootnoteType.USER,
    val paragraphs: List<Paragraph>,
)

// Port of: src/patcher/from-docx.ts (`IPatch` union — ParagraphPatch
// + FilePatch).
package io.docxkt.patcher

import io.docxkt.api.ParagraphSnippets
import io.docxkt.api.RunSnippets
import io.docxkt.api.TableRowSnippets
import io.docxkt.model.drawing.ImageFormat

/**
 * A patch describing what content replaces a `{{key}}` placeholder
 * in the template.
 *
 * Sealed hierarchy:
 *
 * - [Text] — inline text replacement.
 * - [Paragraphs] — replace the marker's enclosing paragraph with N
 *   new paragraphs.
 * - [Image] — inline image embed.
 * - [Rows] — table-row injection.
 * - [ParagraphInline] — INLINE replacement at the marker position
 *   (preserves surrounding paragraph content).
 */
public sealed class Patch {

    /**
     * Replaces the `{{key}}` marker with [value] verbatim.
     *
     * The replacement text inherits the formatting of the FIRST run
     * that contributed any character of the marker (matches
     * upstream's `keepOriginalStyles: true` default). For markers
     * fully inside one run, that's just the run itself. For
     * spanning markers, formatting is taken from the run that
     * contained the opening `{{`.
     *
     * Empty [value] removes the marker without inserting anything.
     */
    public data class Text(public val value: String) : Patch()

    /**
     * Replaces the marker's enclosing paragraph (or splits at the
     * marker if the marker sits mid-paragraph) with the [snippets]
     * paragraphs.
     *
     * Two modes:
     *
     * - **Whole-paragraph replace.** When the marker is the
     *   paragraph's only visible text, the source `<w:p>` is
     *   spliced out and the snippets are inserted in its place.
     *   One paragraph becomes [snippets.size] paragraphs.
     * - **Mid-paragraph split.** When the marker is surrounded by
     *   other text, the source paragraph is split at the marker
     *   into a "before" half + "after" half; the snippets are
     *   inserted between them. One paragraph becomes 2 +
     *   [snippets.size] paragraphs.
     *
     * Build [snippets] via the `paragraphs { … }` top-level DSL in
     * `:core`.
     */
    public data class Paragraphs(public val snippets: ParagraphSnippets) : Patch()

    /**
     * Replaces the `{{key}}` marker with an inline image.
     *
     * The marker run is split (prefix run + drawing run + suffix
     * run); the drawing carries a fresh `r:embed` rId that the
     * patcher allocates by scanning
     * `word/_rels/document.xml.rels`. The image bytes land in
     * `word/media/image{N}.{ext}` with N allocated by scanning
     * existing media filenames.
     *
     * Side effects:
     * - `[Content_Types].xml` gains a `<Default Extension="…"
     *   ContentType="…">` entry (deduped per extension).
     * - `word/_rels/document.xml.rels` gains a relationship to
     *   the new media file.
     * - The output ZIP gains the media binary.
     *
     * [bytes] is passed verbatim — no validation, no re-encoding.
     */
    public data class Image(
        public val bytes: ByteArray,
        public val widthEmus: Int,
        public val heightEmus: Int,
        public val format: ImageFormat,
    ) : Patch() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Image) return false
            return bytes.contentEquals(other.bytes) &&
                widthEmus == other.widthEmus &&
                heightEmus == other.heightEmus &&
                format == other.format
        }

        override fun hashCode(): Int {
            var result = bytes.contentHashCode()
            result = 31 * result + widthEmus
            result = 31 * result + heightEmus
            result = 31 * result + format.hashCode()
            return result
        }
    }

    /**
     * Replaces the `<w:tr>` enclosing the marker with the
     * [snippets] rows. Whole-row replacement only — a marker
     * sharing a row with other rows is OK; a marker sharing a
     * cell with other text is not (the marker's enclosing row
     * gets spliced out regardless).
     *
     * Build [snippets] via the `tableRows { row { … } row { … } }`
     * top-level DSL in `:core`.
     */
    public data class Rows(public val snippets: TableRowSnippets) : Patch()

    /**
     * INLINE replacement at the marker position.
     *
     * Splits the marker run into prefix + suffix, then inserts each
     * `<w:r>` from [snippets] between them. Mirrors upstream's
     * `PatchType.PARAGRAPH` behaviour: surrounding text and
     * formatting are preserved; the inserted runs carry their own
     * formatting from the [runs] DSL (no `keepOriginalStyles`
     * inheritance — upstream copies the source run's `<w:rPr>` onto
     * the inserted runs, which we emit via the snippet builder).
     *
     * Differs from [Paragraphs] which splices/splits the WHOLE
     * paragraph; [ParagraphInline] only touches the marker run
     * inside the paragraph.
     */
    public data class ParagraphInline(public val snippets: RunSnippets) : Patch()
}

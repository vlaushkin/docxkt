// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.footnote.Footnote
import io.docxkt.model.footnote.FootnoteType
import io.docxkt.model.paragraph.Paragraph

/**
 * Configure a single footnote or endnote — a list of paragraphs
 * that will appear inside the matching `<w:footnote>` /
 * `<w:endnote>` wrapper.
 *
 * The first paragraph gets a `FootnoteRef` / `EndnoteRef` marker
 * auto-prepended by the part emitter (not the scope) — matching
 * upstream's `addRunToFront(new FootnoteRefRun())` behaviour.
 */
@DocxktDsl
public class FootnoteScope internal constructor(
    internal val context: DocumentContext,
) {
    private val paragraphs = mutableListOf<Paragraph>()

    /** Add a paragraph to the footnote body. */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        paragraphs += scope.build()
    }

    internal fun build(id: Int): Footnote = Footnote(
        id = id,
        type = FootnoteType.USER,
        paragraphs = paragraphs.toList(),
    )
}

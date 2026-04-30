// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.paragraph.Paragraph
import io.docxkt.model.textbox.TextboxBodyMargins
import io.docxkt.model.textbox.VerticalAnchor

/**
 * Configure a textbox — paragraphs inside `<w:txbxContent>`
 * plus optional `<wps:bodyPr>` attributes (margins, vertical
 * anchor).
 */
@DocxktDsl
public class TextboxScope internal constructor(
    internal val context: DocumentContext,
) {
    private val paragraphs = mutableListOf<Paragraph>()

    /** Vertical text anchor inside the shape. Default: omitted. */
    public var verticalAnchor: VerticalAnchor? = null

    /** Per-side body margins (EMU). Default: all `null` (omit attrs). */
    public var bodyMargins: TextboxBodyMargins = TextboxBodyMargins()

    /**
     * Add a paragraph to the textbox body. Reuses
     * [ParagraphScope] verbatim — runs, formatting, hyperlinks
     * and other inline children all work normally inside the
     * textbox.
     */
    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        paragraphs += scope.build()
    }

    internal fun buildParagraphs(): List<Paragraph> = paragraphs.toList()
}

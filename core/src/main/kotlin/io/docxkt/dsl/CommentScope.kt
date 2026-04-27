// No upstream analogue — DSL scope receivers are a Kotlin idiom.
package io.docxkt.dsl

import io.docxkt.model.comment.Comment
import io.docxkt.model.paragraph.Paragraph

/**
 * Configure a single comment body — a list of paragraphs that
 * will appear inside the `<w:comment>` wrapper in
 * `word/comments.xml`.
 */
@DocxktDsl
public class CommentScope internal constructor(
    internal val context: DocumentContext,
) {
    private val paragraphs = mutableListOf<Paragraph>()

    public fun paragraph(configure: ParagraphScope.() -> Unit) {
        val scope = ParagraphScope(context)
        scope.configure()
        paragraphs += scope.build()
    }

    internal fun build(
        id: Int, author: String?, initials: String?, date: String,
    ): Comment = Comment(
        id = id,
        author = author,
        initials = initials,
        date = date,
        paragraphs = paragraphs.toList(),
    )
}

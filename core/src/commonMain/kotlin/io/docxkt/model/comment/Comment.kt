// Port of: src/file/paragraph/run/comment-run.ts (Comment).
package io.docxkt.model.comment

import io.docxkt.model.paragraph.Paragraph

/**
 * A single `<w:comment>` body — the value registered via
 * `DocumentScope.comment(id, author, initials, date) { … }`.
 *
 * Attribute order on the opening `<w:comment>` tag:
 * `w:id` → `w:initials` → `w:author` → `w:date`. Matches
 * upstream's `CommentAttributes.xmlKeys` order.
 *
 * [date] is a W3CDTF string (e.g. `"2026-04-24T00:00:00.000Z"`).
 * Upstream uses `new Date().toISOString()` at construction time;
 * we require an explicit string so fixture tests can pin a
 * sentinel value.
 */
internal data class Comment(
    val id: Int,
    val author: String? = null,
    val initials: String? = null,
    val date: String,
    val paragraphs: List<Paragraph>,
)

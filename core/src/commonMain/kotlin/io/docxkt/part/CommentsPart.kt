// Port of: src/file/paragraph/run/comment-run.ts (Comments).
package io.docxkt.part

import io.docxkt.model.comment.Comment
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `word/comments.xml` — the comments collection.
 *
 * Root declares 31 namespaces in upstream's specific order (no
 * `wpc`, no `mc:Ignorable` attr). Each `<w:comment>` carries
 * `w:id`, optional `w:initials`, optional `w:author`, and
 * `w:date` (W3CDTF string).
 */
internal class CommentsPart(
    val comments: List<Comment>,
) {
    val path: String = "word/comments.xml"

    val isNonEmpty: Boolean get() = comments.isNotEmpty()

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = Namespaces.COMMENTS_ROOT_NAMESPACES.toTypedArray<Pair<String, String?>>()
        out.openElement("w:comments", *attrs)
        for (c in comments) emitComment(out, c)
        out.closeElement("w:comments")
    }

    private fun emitComment(out: Appendable, c: Comment) {
        out.openElement(
            "w:comment",
            "w:id" to c.id.toString(),
            "w:initials" to c.initials,
            "w:author" to c.author,
            "w:date" to c.date,
        )
        for (p in c.paragraphs) p.appendXml(out)
        out.closeElement("w:comment")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().encodeToByteArray()
}

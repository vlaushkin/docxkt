// Port of: src/file/paragraph/links/bookmark.ts (BookmarkEnd) +
// bookmark-attributes.ts (BookmarkEndAttributes).
package io.docxkt.model.bookmark

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:bookmarkEnd w:id="N"/>` — closes a bookmark range previously
 * opened by a [BookmarkStart] with the same numeric [id].
 *
 * Only one attribute (`w:id`); OOXML allows an optional
 * `w:displacedByCustomXml` we don't emit.
 */
internal class BookmarkEnd(
    val id: Int,
) : XmlComponent("w:bookmarkEnd") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:bookmarkEnd",
            "w:id" to id.toString(),
        )
    }
}

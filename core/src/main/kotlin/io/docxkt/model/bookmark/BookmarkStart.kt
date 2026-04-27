// Port of: src/file/paragraph/links/bookmark.ts (BookmarkStart) +
// bookmark-attributes.ts (BookmarkStartAttributes).
package io.docxkt.model.bookmark

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.selfClosingElement

/**
 * `<w:bookmarkStart w:name="…" w:id="N"/>` — opens a bookmark
 * range inside a paragraph. The matching [BookmarkEnd] closes the
 * range; both must carry the same numeric [id].
 *
 * Attribute order is `w:name` then `w:id` — matches the order in
 * which upstream's `BookmarkStart` constructor populates the
 * `BookmarkStartAttributes` options object
 * (`{ name: id, id: linkId }`), not the order in xmlKeys.
 * Goldens depend on this order.
 */
internal class BookmarkStart(
    val id: Int,
    val name: String,
) : XmlComponent("w:bookmarkStart") {

    override fun appendXml(out: Appendable) {
        out.selfClosingElement(
            "w:bookmarkStart",
            "w:name" to name,
            "w:id" to id.toString(),
        )
    }
}

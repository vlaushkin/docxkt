// Port of: src/file/paragraph/properties.ts (ParagraphPropertiesChange) +
//          src/file/paragraph/run/properties.ts (RunPropertiesChange).
package io.docxkt.model.revision

/**
 * `<w:rPrChange>` / `<w:pPrChange>` attributes — id, author, date.
 * Same shape on every revision wrapper.
 */
public data class RevisionInfo(
    val id: Int,
    val author: String,
    val date: String,
) {
    /** Attributes for the wrapping element; pass to `openElement(...)` via spread. */
    internal fun attrs(): Array<Pair<String, String?>> = arrayOf(
        "w:id" to id.toString(),
        "w:author" to author,
        "w:date" to date,
    )
}

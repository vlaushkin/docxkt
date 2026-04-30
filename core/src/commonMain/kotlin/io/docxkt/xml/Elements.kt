// No upstream analogue — dolanmiu/docx builds an intermediate object tree
// and serializes via the `xml` npm package. We write directly to an
// Appendable and need small primitives for that.
package io.docxkt.xml

/**
 * Low-level element-emission helpers. Attributes are vararg pairs; a `null`
 * value skips the attribute entirely so callers can hand optional fields
 * without conditional branching.
 *
 * These write directly to [Appendable] — no intermediate tree.
 */

internal fun Appendable.appendXmlDeclaration(standalone: Boolean = false) {
    if (standalone) {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>")
    } else {
        append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
    }
}

/**
 * Open a start tag and append its attributes; does NOT close the `>`.
 * Used when the caller wants to decide between self-closing and
 * containing children without buffering.
 */
private fun Appendable.appendTagOpen(name: String, attrs: Array<out Pair<String, String?>>) {
    append('<')
    append(name)
    for ((attrName, attrValue) in attrs) {
        if (attrValue == null) continue
        append(' ')
        append(attrName)
        append("=\"")
        append(XmlEscape.escapeAttributeValue(attrValue))
        append('"')
    }
}

/**
 * Emit an opening tag with attributes: `<name attr="v">`. Attributes with
 * `null` values are skipped.
 */
internal fun Appendable.openElement(name: String, vararg attrs: Pair<String, String?>) {
    appendTagOpen(name, attrs)
    append('>')
}

/** Emit a closing tag: `</name>`. */
internal fun Appendable.closeElement(name: String) {
    append("</")
    append(name)
    append('>')
}

/**
 * Emit a self-closing tag: `<name attr="v"/>`. Attributes with `null` values
 * are skipped.
 */
internal fun Appendable.selfClosingElement(name: String, vararg attrs: Pair<String, String?>) {
    appendTagOpen(name, attrs)
    append("/>")
}

/**
 * Emit a full element containing only escaped text: `<name>escaped</name>`.
 * For `<w:t>`-style elements with inner text content.
 */
internal fun Appendable.textElement(
    name: String,
    text: String,
    vararg attrs: Pair<String, String?>,
) {
    appendTagOpen(name, attrs)
    append('>')
    append(XmlEscape.escapeText(text))
    closeElement(name)
}

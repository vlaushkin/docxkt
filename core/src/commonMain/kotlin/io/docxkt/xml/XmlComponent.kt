// Port of: src/file/xml-components/base.ts:53 +
//          src/file/xml-components/xml-component.ts:45
// dolanmiu's version builds an IXmlableObject tree via prepForXml(); we
// write directly to an Appendable; no intermediate object tree.
package io.docxkt.xml

/**
 * Base class for every XML-producing type in the port.
 *
 * Concrete subclasses write their opening tag, attributes, children, and
 * closing tag directly to the passed [Appendable] in [appendXml]. There is
 * no intermediate tree; element and attribute order are the subclass's
 * responsibility.
 */
internal abstract class XmlComponent(
    protected val elementName: String,
) {
    abstract fun appendXml(out: Appendable)
}

// Port of: src/file/xml-components/xml-component.ts:159-187
/**
 * Variant for property containers (`<w:rPr>`, `<w:pPr>`, `<w:tblPr>`, ...).
 *
 * When the container has no child properties set, the whole element is
 * omitted from output — an empty `<w:rPr/>` must never be emitted.
 * Subclasses implement [isEmpty] and [writeNonEmpty]; the base class
 * short-circuits [appendXml] when empty.
 */
internal abstract class IgnoreIfEmptyXmlComponent(
    elementName: String,
) : XmlComponent(elementName) {

    protected abstract fun isEmpty(): Boolean

    protected abstract fun writeNonEmpty(out: Appendable)

    final override fun appendXml(out: Appendable) {
        if (isEmpty()) return
        writeNonEmpty(out)
    }
}

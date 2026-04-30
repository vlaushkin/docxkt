// Port of: src/file/paragraph/run/run-components/text.ts (L48-L60)
package io.docxkt.model.paragraph.run

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.textElement

/**
 * A `<w:t>` text element — the innermost XML inside a run.
 *
 * Always emits `xml:space="preserve"` so Word does not collapse leading,
 * trailing, or internal whitespace. Upstream emits it unconditionally
 * (`text.ts`), and it's free for us to do the same.
 */
internal class Text(
    val value: String,
) : XmlComponent("w:t") {

    override fun appendXml(out: Appendable) {
        out.textElement(elementName, value, "xml:space" to "preserve")
    }
}

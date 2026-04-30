// Port of: src/file/track-revision/track-revision-components/deleted-text.ts
// (DeletedText). Used inside <w:del> wrappers in place of <w:t>.
package io.docxkt.model.revision

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.textElement

/**
 * `<w:delText xml:space="preserve">…</w:delText>` — deleted-text
 * analogue of `<w:t>`. Always emits `xml:space="preserve"` per
 * upstream's hardcoded `SpaceType.PRESERVE`.
 */
internal class DeletedText(
    val value: String,
) : XmlComponent("w:delText") {

    override fun appendXml(out: Appendable) {
        out.textElement("w:delText", value, "xml:space" to "preserve")
    }
}

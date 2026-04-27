// Port of: src/file/styles/styles.ts (Styles root). Carries an
// optional <w:docDefaults> child for the `documentDefaults { ... }` DSL.
package io.docxkt.part

import io.docxkt.model.style.DocumentDefaults
import io.docxkt.model.style.Style
import io.docxkt.xml.Namespaces
import io.docxkt.xml.appendXmlDeclaration
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement

/**
 * `word/styles.xml` — user-declared paragraph / character styles
 * + optional `<w:docDefaults>` block.
 *
 * Emits `<?xml standalone="yes"?>` + `<w:styles mc:Ignorable="w14 w15"
 * xmlns:mc=… …>` root with the narrow 5-namespace set upstream uses
 * for this part. `<w:docDefaults>` (when present) precedes user
 * styles — matches upstream order.
 *
 * Emitted only when at least one style or document default is
 * registered ([isNonEmpty]).
 */
internal class StylesPart(
    val styles: List<Style>,
    val documentDefaults: DocumentDefaults? = null,
) {
    val path: String = "word/styles.xml"

    val isNonEmpty: Boolean get() = styles.isNotEmpty() || documentDefaults != null

    fun appendXml(out: Appendable) {
        out.appendXmlDeclaration(standalone = true)
        val attrs = buildList<Pair<String, String?>> {
            add("mc:Ignorable" to Namespaces.STYLES_MC_IGNORABLE)
            addAll(Namespaces.STYLES_ROOT_NAMESPACES)
        }.toTypedArray()
        out.openElement("w:styles", *attrs)
        documentDefaults?.appendXml(out)
        for (style in styles) style.appendXml(out)
        out.closeElement("w:styles")
    }

    fun toBytes(): ByteArray = StringBuilder().apply { appendXml(this) }
        .toString().toByteArray(Charsets.UTF_8)
}

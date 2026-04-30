// Port of: src/file/document/body/section-properties/properties/page-borders.ts
package io.docxkt.model.section

import io.docxkt.model.border.BorderSide
import io.docxkt.model.border.writeBorderSide
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.openElement

/** `<w:pgBorders w:display>` value — which pages display the border. */
public enum class PageBorderDisplay(internal val wire: String) {
    ALL_PAGES("allPages"),
    FIRST_PAGE("firstPage"),
    NOT_FIRST_PAGE("notFirstPage"),
}

/** `<w:pgBorders w:offsetFrom>` — origin of the offset measurement. */
public enum class PageBorderOffsetFrom(internal val wire: String) {
    PAGE("page"),
    TEXT("text"),
}

/** `<w:pgBorders w:zOrder>` — z-order relative to page contents. */
public enum class PageBorderZOrder(internal val wire: String) {
    FRONT("front"),
    BACK("back"),
}

/**
 * `<w:pgBorders>` — page borders inside a `<w:sectPr>`. Carries
 * up to four sides plus three attribute-level knobs.
 *
 * Wire shape:
 * ```xml
 * <w:pgBorders w:display="..." w:offsetFrom="..." w:zOrder="...">
 *   <w:top    w:val=... .../>
 *   <w:left   .../>
 *   <w:bottom .../>
 *   <w:right  .../>
 * </w:pgBorders>
 * ```
 *
 * Side order is `top → left → bottom → right` (XSD canonical;
 * distinct from the paragraph-border `top → bottom → left →
 * right` order).
 *
 * Attribute order is `display → offsetFrom → zOrder`, matching
 * upstream's `PageBordersAttributes.xmlKeys`. Not alphabetical.
 *
 * If [top], [left], [bottom], [right] are all null AND no
 * attributes are set, the element should not be constructed
 * (caller suppresses upstream — `IgnoreIfEmptyXmlComponent`).
 * We don't suppress here; the [SectionProperties] caller skips
 * emission when the field is null.
 */
internal class PageBorders(
    val top: BorderSide? = null,
    val left: BorderSide? = null,
    val bottom: BorderSide? = null,
    val right: BorderSide? = null,
    val display: PageBorderDisplay? = null,
    val offsetFrom: PageBorderOffsetFrom? = null,
    val zOrder: PageBorderZOrder? = null,
) : XmlComponent("w:pgBorders") {

    override fun appendXml(out: Appendable) {
        out.openElement(
            "w:pgBorders",
            "w:display" to display?.wire,
            "w:offsetFrom" to offsetFrom?.wire,
            "w:zOrder" to zOrder?.wire,
        )
        top?.let { writeBorderSide(out, "w:top", it) }
        left?.let { writeBorderSide(out, "w:left", it) }
        bottom?.let { writeBorderSide(out, "w:bottom", it) }
        right?.let { writeBorderSide(out, "w:right", it) }
        out.append("</w:pgBorders>")
    }
}

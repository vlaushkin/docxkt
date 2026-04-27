// Port of: src/file/document/body/section-properties/properties/header-footer-reference.ts
package io.docxkt.model.section

/**
 * `<w:headerReference w:type>` / `<w:footerReference w:type>`
 * value — the page(s) on which the referenced header/footer
 * displays.
 *
 * - [DEFAULT] — header/footer for default pages (also odd
 *   pages when even headers are used).
 * - [FIRST] — first page only. Requires `<w:titlePg/>` in the
 *   section.
 * - [EVEN] — even pages only. Requires
 *   `<w:evenAndOddHeaders/>` in `word/settings.xml`.
 */
public enum class HeaderFooterReferenceType(internal val wire: String) {
    DEFAULT("default"),
    FIRST("first"),
    EVEN("even"),
}

// Port of: src/file/document/body/section-properties/properties/section-type.ts
package io.docxkt.model.section

/**
 * `<w:type w:val="…"/>` — how a section begins relative to the
 * previous section. Emitted as the first child of `<w:sectPr>` when set.
 */
public enum class SectionType(internal val wire: String) {
    NEXT_PAGE("nextPage"),
    NEXT_COLUMN("nextColumn"),
    CONTINUOUS("continuous"),
    EVEN_PAGE("evenPage"),
    ODD_PAGE("oddPage"),
}

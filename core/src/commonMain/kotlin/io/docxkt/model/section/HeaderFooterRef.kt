// Port of: src/file/document/body/section-properties/properties/header-footer-reference.ts
package io.docxkt.model.section

/**
 * One `<w:headerReference>` or `<w:footerReference>` inside a
 * `<w:sectPr>`. Carries the type (default / first / even) and the
 * rId pointing at the header/footer part. A section may carry
 * multiple refs (different types) but at most one per type.
 */
internal data class HeaderFooterRef(
    val type: HeaderFooterReferenceType,
    val rid: String,
)

// Port of: src/file/document/body/section-properties/page-size/page-orientation.ts
package io.docxkt.model.section

/** Value of `<w:pgSz w:orient="...">`. */
public enum class PageOrientation(internal val wire: String) {
    PORTRAIT("portrait"),
    LANDSCAPE("landscape"),
}

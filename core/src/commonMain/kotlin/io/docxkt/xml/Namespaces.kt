// Port of: src/file/document/document-attributes.ts (xmlns collection), plus
// URI constants scattered across src/file/xml-components/.
package io.docxkt.xml

/**
 * Single source of truth for OOXML namespace URIs and prefixes
 * used by both `:core` and `:patcher` modules.
 *
 * Public because the patcher module needs access to the same URIs
 * — duplicating the strings across modules violates the SoT
 * requirement (audit code-quality §6 / patcher MINOR-6). These are
 * stable wire identifiers; their values are not v1.0-volatile.
 *
 * The full set of `xmlns:*` declarations emitted on a `<w:document>`
 * root is in [DOCUMENT_ROOT_NAMESPACES] etc. Individual parts
 * (content types, rels) have their own defaults declared inline
 * where used.
 */
public object Namespaces {
    public const val WORDPROCESSING_ML: String =
        "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    public const val RELATIONSHIPS_OFFICE_DOCUMENT: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships"
    public const val MATH: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/math"
    public const val MARKUP_COMPATIBILITY: String =
        "http://schemas.openxmlformats.org/markup-compatibility/2006"

    public const val OFFICE: String = "urn:schemas-microsoft-com:office:office"
    public const val VML: String = "urn:schemas-microsoft-com:vml"
    public const val WORD_2010: String = "urn:schemas-microsoft-com:office:word"

    /** DrawingML main namespace — used in `<a:graphic>`, `<a:blip>`, etc. */
    public const val DRAWINGML_MAIN: String =
        "http://schemas.openxmlformats.org/drawingml/2006/main"
    /** DrawingML picture namespace — used in `<pic:pic>` subtree. */
    public const val DRAWINGML_PICTURE: String =
        "http://schemas.openxmlformats.org/drawingml/2006/picture"

    /** Package-relationships namespace — `<Relationships>` xmlns. */
    public const val PACKAGE_RELATIONSHIPS_NAMESPACE: String =
        "http://schemas.openxmlformats.org/package/2006/relationships"
    /** Content-types namespace — `<Types>` xmlns. */
    public const val PACKAGE_CONTENT_TYPES_NAMESPACE: String =
        "http://schemas.openxmlformats.org/package/2006/content-types"

    /** Image relationship type URI. */
    public const val REL_IMAGE: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/image"
    /** Hyperlink relationship type URI. */
    public const val REL_HYPERLINK: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/relationships/hyperlink"

    /**
     * Compatibility-setting URI emitted on `<w:compatSetting w:uri="…"/>`
     * inside `word/settings.xml`. Distinct from [WORD_2010] (the
     * `urn:schemas-microsoft-com:office:word` legacy URN).
     */
    public const val WORD_2010_URI: String = "http://schemas.microsoft.com/office/word"

    /**
     * The W3C XML namespace — used as the namespace URI for
     * `xml:space`, `xml:lang`, `xml:base` attributes. Defined by
     * <https://www.w3.org/XML/1998/namespace>.
     */
    public const val XML_W3C: String = "http://www.w3.org/XML/1998/namespace"

    public const val WORDPROCESSING_CANVAS_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordprocessingCanvas"
    public const val WORDPROCESSING_DRAWING_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordprocessingDrawing"
    public const val WORDPROCESSING_DRAWING: String =
        "http://schemas.openxmlformats.org/drawingml/2006/wordprocessingDrawing"
    public const val WORDPROCESSING_GROUP_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordprocessingGroup"
    public const val WORDPROCESSING_INK_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordprocessingInk"
    public const val WORDPROCESSING_SHAPE_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordprocessingShape"

    public const val WORDML_2006: String =
        "http://schemas.microsoft.com/office/word/2006/wordml"
    public const val WORDML_2010: String =
        "http://schemas.microsoft.com/office/word/2010/wordml"
    public const val WORDML_2012: String =
        "http://schemas.microsoft.com/office/word/2012/wordml"
    public const val WORDML_2016_CID: String =
        "http://schemas.microsoft.com/office/word/2016/wordml/cid"
    public const val WORDML_2018: String =
        "http://schemas.microsoft.com/office/word/2018/wordml"
    public const val WORDML_2018_CEX: String =
        "http://schemas.microsoft.com/office/word/2018/wordml/cex"
    public const val WORDML_2020_SDTDH: String =
        "http://schemas.microsoft.com/office/word/2020/wordml/sdtdatahash"
    public const val WORDML_2015_SYMEX: String =
        "http://schemas.microsoft.com/office/word/2015/wordml/symex"

    public const val DRAWING_CHARTEX_2014: String =
        "http://schemas.microsoft.com/office/drawing/2014/chartex"
    public const val DRAWING_CHARTEX_2015_9_8: String =
        "http://schemas.microsoft.com/office/drawing/2015/9/8/chartex"
    public const val DRAWING_CHARTEX_2015_10_21: String =
        "http://schemas.microsoft.com/office/drawing/2015/10/21/chartex"
    public const val DRAWING_CHARTEX_2016_5_9: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/9/chartex"
    public const val DRAWING_CHARTEX_2016_5_10: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/10/chartex"
    public const val DRAWING_CHARTEX_2016_5_11: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/11/chartex"
    public const val DRAWING_CHARTEX_2016_5_12: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/12/chartex"
    public const val DRAWING_CHARTEX_2016_5_13: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/13/chartex"
    public const val DRAWING_CHARTEX_2016_5_14: String =
        "http://schemas.microsoft.com/office/drawing/2016/5/14/chartex"
    public const val DRAWING_INK_2016: String =
        "http://schemas.microsoft.com/office/drawing/2016/ink"
    public const val DRAWING_MODEL3D_2017: String =
        "http://schemas.microsoft.com/office/drawing/2017/model3d"

    public const val PACKAGE_RELATIONSHIPS: String =
        "http://schemas.openxmlformats.org/package/2006/relationships"
    public const val PACKAGE_CONTENT_TYPES: String =
        "http://schemas.openxmlformats.org/package/2006/content-types"
    public const val PACKAGE_CORE_PROPERTIES: String =
        "http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties"
    public const val OFFICE_DOCUMENT_RELATIONSHIP: String =
        "$RELATIONSHIPS_OFFICE_DOCUMENT/officeDocument"

    /**
     * The xmlns declarations emitted on the root `<w:document>` element, in
     * the exact order upstream emits them. Order is not semantically
     * significant to Word, but preserving it keeps golden-fixture diffs
     * clean.
     */
    public val DOCUMENT_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:wpc" to WORDPROCESSING_CANVAS_2010,
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:o" to OFFICE,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:m" to MATH,
        "xmlns:v" to VML,
        "xmlns:wp14" to WORDPROCESSING_DRAWING_2010,
        "xmlns:wp" to WORDPROCESSING_DRAWING,
        "xmlns:w10" to WORD_2010,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
        "xmlns:wpg" to WORDPROCESSING_GROUP_2010,
        "xmlns:wpi" to WORDPROCESSING_INK_2010,
        "xmlns:wne" to WORDML_2006,
        "xmlns:wps" to WORDPROCESSING_SHAPE_2010,
        "xmlns:cx" to DRAWING_CHARTEX_2014,
        "xmlns:cx1" to DRAWING_CHARTEX_2015_9_8,
        "xmlns:cx2" to DRAWING_CHARTEX_2015_10_21,
        "xmlns:cx3" to DRAWING_CHARTEX_2016_5_9,
        "xmlns:cx4" to DRAWING_CHARTEX_2016_5_10,
        "xmlns:cx5" to DRAWING_CHARTEX_2016_5_11,
        "xmlns:cx6" to DRAWING_CHARTEX_2016_5_12,
        "xmlns:cx7" to DRAWING_CHARTEX_2016_5_13,
        "xmlns:cx8" to DRAWING_CHARTEX_2016_5_14,
        "xmlns:aink" to DRAWING_INK_2016,
        "xmlns:am3d" to DRAWING_MODEL3D_2017,
        "xmlns:w16cex" to WORDML_2018_CEX,
        "xmlns:w16cid" to WORDML_2016_CID,
        "xmlns:w16" to WORDML_2018,
        "xmlns:w16sdtdh" to WORDML_2020_SDTDH,
        "xmlns:w16se" to WORDML_2015_SYMEX,
    )

    /** `mc:Ignorable` value emitted on `<w:document>`. */
    public const val DOCUMENT_MC_IGNORABLE: String = "w14 w15 wp14"

    /**
     * Namespace declarations on the `<w:hdr>` root. Upstream emits a
     * subset of the document's set — same base 16 plus `cx`-variants
     * and `w16cid` / `w16se`, but without `aink` / `am3d` / `w16cex` /
     * `w16` / `w16sdtdh`. Byte-for-byte match with upstream.
     */
    public val HEADER_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:wpc" to WORDPROCESSING_CANVAS_2010,
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:o" to OFFICE,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:m" to MATH,
        "xmlns:v" to VML,
        "xmlns:wp14" to WORDPROCESSING_DRAWING_2010,
        "xmlns:wp" to WORDPROCESSING_DRAWING,
        "xmlns:w10" to WORD_2010,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
        "xmlns:wpg" to WORDPROCESSING_GROUP_2010,
        "xmlns:wpi" to WORDPROCESSING_INK_2010,
        "xmlns:wne" to WORDML_2006,
        "xmlns:wps" to WORDPROCESSING_SHAPE_2010,
        "xmlns:cx" to DRAWING_CHARTEX_2014,
        "xmlns:cx1" to DRAWING_CHARTEX_2015_9_8,
        "xmlns:cx2" to DRAWING_CHARTEX_2015_10_21,
        "xmlns:cx3" to DRAWING_CHARTEX_2016_5_9,
        "xmlns:cx4" to DRAWING_CHARTEX_2016_5_10,
        "xmlns:cx5" to DRAWING_CHARTEX_2016_5_11,
        "xmlns:cx6" to DRAWING_CHARTEX_2016_5_12,
        "xmlns:cx7" to DRAWING_CHARTEX_2016_5_13,
        "xmlns:cx8" to DRAWING_CHARTEX_2016_5_14,
        "xmlns:w16cid" to WORDML_2016_CID,
        "xmlns:w16se" to WORDML_2015_SYMEX,
    )

    /**
     * The base 16-namespace wordprocessingML set — no `cx`, no `w16*`,
     * no `aink` / `am3d`. Upstream emits this set on both `<w:ftr>`
     * and `<w:numbering>` roots (confirmed by probing). Header gets
     * a wider 27-set for historical reasons; we reuse this 16-set
     * wherever upstream does.
     */
    public val FOOTER_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:wpc" to WORDPROCESSING_CANVAS_2010,
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:o" to OFFICE,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:m" to MATH,
        "xmlns:v" to VML,
        "xmlns:wp14" to WORDPROCESSING_DRAWING_2010,
        "xmlns:wp" to WORDPROCESSING_DRAWING,
        "xmlns:w10" to WORD_2010,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
        "xmlns:wpg" to WORDPROCESSING_GROUP_2010,
        "xmlns:wpi" to WORDPROCESSING_INK_2010,
        "xmlns:wne" to WORDML_2006,
        "xmlns:wps" to WORDPROCESSING_SHAPE_2010,
    )

    /**
     * The narrow 5-namespace set upstream emits on `<w:styles>` — no
     * drawing / ink / chart / VML. Matches upstream's
     * `DefaultStylesFactory.newInstance()` call
     * `new DocumentAttributes(["mc", "r", "w", "w14", "w15"], "w14 w15")`.
     */
    public val STYLES_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
    )

    /** `mc:Ignorable` value emitted on `<w:styles>`. */
    public const val STYLES_MC_IGNORABLE: String = "w14 w15"

    /**
     * Namespace declarations on `<w:fonts>` root of
     * `word/fontTable.xml`. Upstream's `createFontTable()` emits a
     * distinct 10-ns set that includes the modern Word-2018/2020
     * markup-compat extensions (`w16cex`, `w16cid`, `w16`,
     * `w16sdtdh`, `w16se`) alongside the base ooxml namespaces.
     * Neither `o` nor `v` nor `wp*` — different from both the
     * footer/numbering subset and the styles narrow subset.
     */
    public val FONT_TABLE_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
        "xmlns:w16cex" to WORDML_2018_CEX,
        "xmlns:w16cid" to WORDML_2016_CID,
        "xmlns:w16" to WORDML_2018,
        "xmlns:w16sdtdh" to WORDML_2020_SDTDH,
        "xmlns:w16se" to WORDML_2015_SYMEX,
    )

    /** `mc:Ignorable` on `<w:fonts>` — upstream hardcodes this ordering. */
    public const val FONT_TABLE_MC_IGNORABLE: String = "w14 w15 w16se w16cid w16 w16cex w16sdtdh"

    /**
     * Namespace declarations on the docProps `<cp:coreProperties>` root.
     * Five-entry fixed set: cp, dc, dcterms, dcmitype, xsi.
     */
    public val CORE_PROPERTIES_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:cp" to CORE_PROPERTIES_CP,
        "xmlns:dc" to DUBLIN_CORE_ELEMENTS,
        "xmlns:dcterms" to DUBLIN_CORE_TERMS,
        "xmlns:dcmitype" to DUBLIN_CORE_DCMITYPE,
        "xmlns:xsi" to XSI,
    )

    public const val CORE_PROPERTIES_CP: String =
        "http://schemas.openxmlformats.org/package/2006/metadata/core-properties"
    public const val DUBLIN_CORE_ELEMENTS: String = "http://purl.org/dc/elements/1.1/"
    public const val DUBLIN_CORE_TERMS: String = "http://purl.org/dc/terms/"
    public const val DUBLIN_CORE_DCMITYPE: String = "http://purl.org/dc/dcmitype/"
    public const val XSI: String = "http://www.w3.org/2001/XMLSchema-instance"

    /** Extended (app-properties) namespace — default xmlns on `<Properties>`. */
    public const val EXTENDED_PROPERTIES: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/extended-properties"
    /** Variant type namespace used inside docProps/app.xml and custom.xml. */
    public const val DOC_PROPS_VTYPES: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/docPropsVTypes"
    /** Custom-properties namespace — default xmlns on `<Properties>`. */
    public const val CUSTOM_PROPERTIES: String =
        "http://schemas.openxmlformats.org/officeDocument/2006/custom-properties"

    /**
     * Namespace declarations on `<w:comments>` root of
     * `word/comments.xml`. 31-entry set in upstream's
     * `RootCommentsAttributes.xmlKeys` declaration order: cx,
     * cx1–cx8 first, then mc/aink/am3d, the base
     * wordprocessingml set, then w16 variants, then
     * wpg/wpi/wne/wps.
     *
     * No `wpc` — comments.xml is the only OOXML root we emit
     * that omits the wordprocessingCanvas namespace. No
     * `mc:Ignorable` attribute either.
     */
    public val COMMENTS_ROOT_NAMESPACES: List<Pair<String, String>> = listOf(
        "xmlns:cx" to DRAWING_CHARTEX_2014,
        "xmlns:cx1" to DRAWING_CHARTEX_2015_9_8,
        "xmlns:cx2" to DRAWING_CHARTEX_2015_10_21,
        "xmlns:cx3" to DRAWING_CHARTEX_2016_5_9,
        "xmlns:cx4" to DRAWING_CHARTEX_2016_5_10,
        "xmlns:cx5" to DRAWING_CHARTEX_2016_5_11,
        "xmlns:cx6" to DRAWING_CHARTEX_2016_5_12,
        "xmlns:cx7" to DRAWING_CHARTEX_2016_5_13,
        "xmlns:cx8" to DRAWING_CHARTEX_2016_5_14,
        "xmlns:mc" to MARKUP_COMPATIBILITY,
        "xmlns:aink" to DRAWING_INK_2016,
        "xmlns:am3d" to DRAWING_MODEL3D_2017,
        "xmlns:o" to OFFICE,
        "xmlns:r" to RELATIONSHIPS_OFFICE_DOCUMENT,
        "xmlns:m" to MATH,
        "xmlns:v" to VML,
        "xmlns:wp14" to WORDPROCESSING_DRAWING_2010,
        "xmlns:wp" to WORDPROCESSING_DRAWING,
        "xmlns:w10" to WORD_2010,
        "xmlns:w" to WORDPROCESSING_ML,
        "xmlns:w14" to WORDML_2010,
        "xmlns:w15" to WORDML_2012,
        "xmlns:w16cex" to WORDML_2018_CEX,
        "xmlns:w16cid" to WORDML_2016_CID,
        "xmlns:w16" to WORDML_2018,
        "xmlns:w16sdtdh" to WORDML_2020_SDTDH,
        "xmlns:w16se" to WORDML_2015_SYMEX,
        "xmlns:wpg" to WORDPROCESSING_GROUP_2010,
        "xmlns:wpi" to WORDPROCESSING_INK_2010,
        "xmlns:wne" to WORDML_2006,
        "xmlns:wps" to WORDPROCESSING_SHAPE_2010,
    )
}

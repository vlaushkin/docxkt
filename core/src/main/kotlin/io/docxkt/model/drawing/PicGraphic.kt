// Helper that emits the <a:graphic>/<pic:pic> subtree identical
// between inline (<wp:inline>) and floating (<wp:anchor>) drawings.
//
// Port of: src/file/drawing/inline/graphic.ts +
//          src/file/drawing/inline/graphic-data.ts +
//          src/file/drawing/inline/pic.ts +
//          src/file/drawing/inline/pic-blip-fill.ts +
//          src/file/drawing/inline/pic-sp-pr.ts +
//          src/file/drawing/inline/pic-non-visual-pic-properties.ts
package io.docxkt.model.drawing

import io.docxkt.xml.Namespaces
import io.docxkt.xml.selfClosingElement

internal const val NS_DRAWINGML_A: String = Namespaces.DRAWINGML_MAIN
internal const val NS_DRAWINGML_PIC: String = Namespaces.DRAWINGML_PICTURE

/**
 * Emit the `<a:graphic>` → `<a:graphicData>` → `<pic:pic>` subtree
 * shared by inline and floating drawings. Output is identical
 * regardless of wrapper element (`<wp:inline>` or `<wp:anchor>`).
 *
 * Caller is responsible for the surrounding wrapper. The subtree
 * starts with `<a:graphic xmlns:a="…">` and ends with
 * `</a:graphic>`.
 */
internal fun appendPicGraphic(
    out: Appendable,
    embedRid: String,
    widthEmus: Int,
    heightEmus: Int,
) {
    out.append("<a:graphic xmlns:a=\"").append(NS_DRAWINGML_A).append("\">")
    out.append("<a:graphicData uri=\"").append(NS_DRAWINGML_PIC).append("\">")
    out.append("<pic:pic xmlns:pic=\"").append(NS_DRAWINGML_PIC).append("\">")

    // nvPicPr
    out.append("<pic:nvPicPr>")
    out.selfClosingElement(
        "pic:cNvPr",
        "id" to "0",
        "name" to "",
        "descr" to "",
    )
    out.append("<pic:cNvPicPr>")
    out.selfClosingElement(
        "a:picLocks",
        "noChangeAspect" to "1",
        "noChangeArrowheads" to "1",
    )
    out.append("</pic:cNvPicPr>")
    out.append("</pic:nvPicPr>")

    // blipFill
    out.append("<pic:blipFill>")
    out.selfClosingElement(
        "a:blip",
        "r:embed" to embedRid,
        "cstate" to "none",
    )
    out.selfClosingElement("a:srcRect")
    out.append("<a:stretch>")
    out.selfClosingElement("a:fillRect")
    out.append("</a:stretch>")
    out.append("</pic:blipFill>")

    // spPr
    out.append("<pic:spPr bwMode=\"auto\">")
    out.append("<a:xfrm>")
    out.selfClosingElement("a:off", "x" to "0", "y" to "0")
    out.selfClosingElement(
        "a:ext",
        "cx" to widthEmus.toString(),
        "cy" to heightEmus.toString(),
    )
    out.append("</a:xfrm>")
    out.append("<a:prstGeom prst=\"rect\">")
    out.selfClosingElement("a:avLst")
    out.append("</a:prstGeom>")
    out.append("</pic:spPr>")

    out.append("</pic:pic>")
    out.append("</a:graphicData>")
    out.append("</a:graphic>")
}

/**
 * Emit the shared block AFTER `wp:effectExtent` (and after the
 * wrap element for floating drawings) — `<wp:docPr>` +
 * `<wp:cNvGraphicFramePr>`. Inline and floating use identical
 * shape.
 */
internal fun appendDocPrAndGraphicFrameProperties(
    out: Appendable,
    docPrId: Int,
    description: String?,
) {
    out.selfClosingElement(
        "wp:docPr",
        "id" to docPrId.toString(),
        "name" to "",
        "descr" to (description ?: ""),
        "title" to "",
    )
    out.append("<wp:cNvGraphicFramePr>")
    out.selfClosingElement(
        "a:graphicFrameLocks",
        "xmlns:a" to NS_DRAWINGML_A,
        "noChangeAspect" to "1",
    )
    out.append("</wp:cNvGraphicFramePr>")
}

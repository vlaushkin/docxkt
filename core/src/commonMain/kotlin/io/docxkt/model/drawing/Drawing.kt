// Port of: src/file/drawing/drawing.ts +
//          src/file/drawing/inline/inline.ts +
//          src/file/drawing/extent/extent.ts +
//          src/file/drawing/doc-properties/doc-properties.ts +
//          src/file/drawing/graphic-frame/graphic-frame.ts
package io.docxkt.model.drawing

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

/**
 * `<w:drawing>` — the DrawingML wrapper for an inline raster image.
 *
 * Emits the minimal tree upstream produces for a single inline
 * image: `<wp:inline>` → `<wp:extent>` → `<wp:effectExtent>` →
 * `<wp:docPr>` → `<wp:cNvGraphicFramePr>` → `<a:graphic>` →
 * `<a:graphicData>` → `<pic:pic>` subtree with
 * `<a:blip r:embed="rId{N}"/>`.
 *
 * **Child order is strict.** LibreOffice silently drops the image
 * if `<wp:extent>` doesn't come before `<wp:effectExtent>` etc. The
 * emission follows upstream's hand-crafted order verbatim — do not
 * reorder for "readability".
 *
 * The shared `<a:graphic>/<pic:pic>` subtree is in [appendPicGraphic]
 * so [AnchorDrawing] can reuse it verbatim.
 *
 * Upstream's `wp:docPr id` counter starts at 1 per document;
 * `pic:cNvPr id` is always 0. We follow both conventions — the
 * hardcodes are safe so long as the fixtures use a single image.
 */
internal class Drawing(
    val embedSlot: ImageSlot,
    val widthEmus: Int,
    val heightEmus: Int,
    val docPrId: Int = 1,
    val description: String? = null,
) : XmlComponent("w:drawing") {

    override fun appendXml(out: Appendable) {
        val embedRelationshipId = embedSlot.rid
        out.openElement("w:drawing")
        out.openElement(
            "wp:inline",
            "distT" to "0",
            "distB" to "0",
            "distL" to "0",
            "distR" to "0",
        )

        out.selfClosingElement(
            "wp:extent",
            "cx" to widthEmus.toString(),
            "cy" to heightEmus.toString(),
        )
        out.selfClosingElement(
            "wp:effectExtent",
            "t" to "0",
            "r" to "0",
            "b" to "0",
            "l" to "0",
        )
        appendDocPrAndGraphicFrameProperties(out, docPrId, description)
        appendPicGraphic(out, embedRelationshipId, widthEmus, heightEmus)

        out.closeElement("wp:inline")
        out.closeElement("w:drawing")
    }
}

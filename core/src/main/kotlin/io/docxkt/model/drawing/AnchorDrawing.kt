// Port of: src/file/drawing/anchor/anchor.ts +
//          src/file/drawing/anchor/anchor-attributes.ts +
//          src/file/drawing/floating/horizontal-position.ts +
//          src/file/drawing/floating/vertical-position.ts +
//          src/file/drawing/floating/position-offset.ts +
//          src/file/drawing/floating/align.ts +
//          src/file/drawing/floating/simple-pos.ts +
//          src/file/drawing/text-wrap/wrap-{none,square,tight,top-and-bottom}.ts
package io.docxkt.model.drawing

import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement
import io.docxkt.xml.textElement

/**
 * `<w:drawing>` wrapper for a **floating** raster image — i.e.
 * one positioned via `<wp:anchor>` rather than `<wp:inline>`.
 *
 * Attribute order on `<wp:anchor>` is fixed to match upstream's
 * `AnchorAttributes`: `distT, distB, distL, distR, simplePos,
 * allowOverlap, behindDoc, locked, layoutInCell, relativeHeight`.
 *
 * Child sequence (strict):
 * 1. `<wp:simplePos x="0" y="0"/>` — upstream hardcodes
 *    `simplePos="0"` on the wrapper and emits this child as a
 *    no-op placeholder.
 * 2. `<wp:positionH relativeFrom="…"><wp:posOffset>N</wp:posOffset></wp:positionH>`
 *    or `<wp:align>VAL</wp:align>` body.
 * 3. `<wp:positionV …>` — same shape.
 * 4. `<wp:extent cx cy/>`.
 * 5. `<wp:effectExtent t r b l/>`.
 * 6. Wrap variant element (None → `<wp:wrapNone/>`, etc.).
 * 7. `<wp:docPr id name descr title/>`.
 * 8. `<wp:cNvGraphicFramePr>`.
 * 9. `<a:graphic>` shared subtree (via [appendPicGraphic]).
 *
 * `relativeHeight` defaults to [heightEmus] — matches
 * upstream's `transform.emus.y` fallback. Override via
 * [relativeHeight] when explicit z-order pinning is needed.
 */
internal class AnchorDrawing(
    val embedSlot: ImageSlot,
    val widthEmus: Int,
    val heightEmus: Int,
    val horizontalPosition: HorizontalPosition,
    val verticalPosition: VerticalPosition,
    val wrap: AnchorWrap = AnchorWrap.None,
    val behindDoc: Boolean = false,
    val allowOverlap: Boolean = true,
    val layoutInCell: Boolean = true,
    val lockAnchor: Boolean = false,
    val anchorMargins: AnchorMargins = AnchorMargins(),
    val relativeHeight: Int? = null,
    val docPrId: Int = 1,
    val description: String? = null,
) : XmlComponent("w:drawing") {

    override fun appendXml(out: Appendable) {
        val rid = embedSlot.rid
        out.openElement("w:drawing")
        appendAnchorOpen(out)
        out.selfClosingElement("wp:simplePos", "x" to "0", "y" to "0")
        appendPositionH(out)
        appendPositionV(out)
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
        appendWrap(out)
        appendDocPrAndGraphicFrameProperties(out, docPrId, description)
        appendPicGraphic(out, rid, widthEmus, heightEmus)
        out.closeElement("wp:anchor")
        out.closeElement("w:drawing")
    }

    private fun appendAnchorOpen(out: Appendable) {
        out.openElement(
            "wp:anchor",
            "distT" to anchorMargins.topEmus.toString(),
            "distB" to anchorMargins.bottomEmus.toString(),
            "distL" to anchorMargins.leftEmus.toString(),
            "distR" to anchorMargins.rightEmus.toString(),
            "simplePos" to "0",
            "allowOverlap" to if (allowOverlap) "1" else "0",
            "behindDoc" to if (behindDoc) "1" else "0",
            "locked" to if (lockAnchor) "1" else "0",
            "layoutInCell" to if (layoutInCell) "1" else "0",
            "relativeHeight" to (relativeHeight ?: heightEmus).toString(),
        )
    }

    private fun appendPositionH(out: Appendable) {
        out.openElement("wp:positionH", "relativeFrom" to horizontalPosition.relativeFrom.wire)
        if (horizontalPosition.align != null) {
            out.textElement("wp:align", horizontalPosition.align.wire)
        } else {
            out.textElement("wp:posOffset", horizontalPosition.offsetEmus!!.toString())
        }
        out.closeElement("wp:positionH")
    }

    private fun appendPositionV(out: Appendable) {
        out.openElement("wp:positionV", "relativeFrom" to verticalPosition.relativeFrom.wire)
        if (verticalPosition.align != null) {
            out.textElement("wp:align", verticalPosition.align.wire)
        } else {
            out.textElement("wp:posOffset", verticalPosition.offsetEmus!!.toString())
        }
        out.closeElement("wp:positionV")
    }

    private fun appendWrap(out: Appendable) {
        when (val w = wrap) {
            AnchorWrap.None -> out.selfClosingElement("wp:wrapNone")
            is AnchorWrap.Square -> out.selfClosingElement(
                "wp:wrapSquare",
                "wrapText" to w.side.wire,
                "distT" to w.margins.topEmus.takeIf { it != 0 }?.toString(),
                "distB" to w.margins.bottomEmus.takeIf { it != 0 }?.toString(),
                "distL" to w.margins.leftEmus.takeIf { it != 0 }?.toString(),
                "distR" to w.margins.rightEmus.takeIf { it != 0 }?.toString(),
            )
            is AnchorWrap.Tight -> out.selfClosingElement(
                "wp:wrapTight",
                "distT" to w.marginTopEmus.takeIf { it != 0 }?.toString(),
                "distB" to w.marginBottomEmus.takeIf { it != 0 }?.toString(),
            )
            is AnchorWrap.TopAndBottom -> out.selfClosingElement(
                "wp:wrapTopAndBottom",
                "distT" to w.marginTopEmus.takeIf { it != 0 }?.toString(),
                "distB" to w.marginBottomEmus.takeIf { it != 0 }?.toString(),
            )
        }
    }
}


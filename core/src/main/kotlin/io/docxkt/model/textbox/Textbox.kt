// Port of: src/file/drawing/inline/graphic/graphic-data/wps/wps-shape.ts +
//          src/file/drawing/inline/graphic/graphic-data/wps/wps-text-box.ts +
//          src/file/drawing/inline/graphic/graphic-data/wps/text-box-content.ts +
//          src/file/drawing/inline/graphic/graphic-data/wps/body-properties.ts +
//          src/file/drawing/inline/graphic/graphic-data/wps/non-visual-shape-properties.ts +
//          src/file/paragraph/run/wps-shape-run.ts
package io.docxkt.model.textbox

import io.docxkt.model.drawing.appendDocPrAndGraphicFrameProperties
import io.docxkt.model.paragraph.Paragraph
import io.docxkt.xml.Namespaces
import io.docxkt.xml.XmlComponent
import io.docxkt.xml.closeElement
import io.docxkt.xml.openElement
import io.docxkt.xml.selfClosingElement

private const val NS_DRAWINGML_A: String = Namespaces.DRAWINGML_MAIN
private const val URI_WORDPROCESSING_SHAPE: String = Namespaces.WORDPROCESSING_SHAPE_2010

/**
 * `<w:drawing>` wrapper for a modern DrawingML textbox —
 * `<wp:inline>` containing a `<wps:wsp>` shape with a
 * `<wps:txbx>/<w:txbxContent>` body.
 *
 * The outer `<wp:inline>`/`<wp:extent>`/`<wp:effectExtent>`/
 * `<wp:docPr>`/`<wp:cNvGraphicFramePr>` block is identical
 * to an inline image. Only the inner `<a:graphicData>` URI
 * changes (from picture to wordprocessingShape) and the body
 * becomes `<wps:wsp>`.
 *
 * `<wps:bodyPr>` is emitted self-closed when no body
 * configuration is present (no margins, no anchor).
 *
 * Inline form only — floating (anchor-wrapped `<wps:wsp>`)
 * textboxes are not yet supported.
 */
internal class Textbox(
    val widthEmus: Int,
    val heightEmus: Int,
    val paragraphs: List<Paragraph>,
    val bodyMargins: TextboxBodyMargins = TextboxBodyMargins(),
    val verticalAnchor: VerticalAnchor? = null,
    val docPrId: Int = 1,
    val description: String? = null,
) : XmlComponent("w:drawing") {

    override fun appendXml(out: Appendable) {
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

        // Graphic with the wordprocessingShape graphicData URI.
        out.openElement("a:graphic", "xmlns:a" to NS_DRAWINGML_A)
        out.openElement("a:graphicData", "uri" to URI_WORDPROCESSING_SHAPE)
        appendWpsShape(out)
        out.closeElement("a:graphicData")
        out.closeElement("a:graphic")

        out.closeElement("wp:inline")
        out.closeElement("w:drawing")
    }

    private fun appendWpsShape(out: Appendable) {
        out.openElement("wps:wsp")
        // cNvSpPr — non-visual shape properties; txBox="1" hardcoded.
        out.selfClosingElement("wps:cNvSpPr", "txBox" to "1")
        // spPr — shape properties (size + rect prstGeom).
        out.openElement("wps:spPr", "bwMode" to "auto")
        out.openElement("a:xfrm")
        out.selfClosingElement("a:off", "x" to "0", "y" to "0")
        out.selfClosingElement(
            "a:ext",
            "cx" to widthEmus.toString(),
            "cy" to heightEmus.toString(),
        )
        out.closeElement("a:xfrm")
        out.openElement("a:prstGeom", "prst" to "rect")
        out.selfClosingElement("a:avLst")
        out.closeElement("a:prstGeom")
        out.closeElement("wps:spPr")
        // txbx — body content.
        out.openElement("wps:txbx")
        out.openElement("w:txbxContent")
        for (p in paragraphs) p.appendXml(out)
        out.closeElement("w:txbxContent")
        out.closeElement("wps:txbx")
        // bodyPr — last child. Self-closed if empty.
        appendBodyPr(out)
        out.closeElement("wps:wsp")
    }

    private fun appendBodyPr(out: Appendable) {
        if (bodyMargins.isEmpty && verticalAnchor == null) {
            out.selfClosingElement("wps:bodyPr")
            return
        }
        out.selfClosingElement(
            "wps:bodyPr",
            "lIns" to bodyMargins.leftEmus?.toString(),
            "rIns" to bodyMargins.rightEmus?.toString(),
            "tIns" to bodyMargins.topEmus?.toString(),
            "bIns" to bodyMargins.bottomEmus?.toString(),
            "anchor" to verticalAnchor?.wire,
        )
    }
}

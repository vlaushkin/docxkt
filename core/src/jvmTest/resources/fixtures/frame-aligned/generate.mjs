// Phase 25 second fixture. <w:framePr> with alignment-based
// positioning + drop-cap.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  Document, Packer, Paragraph, TextRun,
  FrameAnchorType, DropCapType,
  HorizontalPositionAlign, VerticalPositionAlign,
} = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      frame: {
        type: "alignment",
        alignment: { x: HorizontalPositionAlign.LEFT, y: VerticalPositionAlign.TOP },
        width: 1440,
        height: 1440,
        anchor: { horizontal: FrameAnchorType.TEXT, vertical: FrameAnchorType.TEXT },
        dropCap: DropCapType.DROP,
        lines: 3,
      },
      children: [new TextRun("Drop-cap framed")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Phase 25 first fixture. <w:framePr> with absolute XY
// positioning and around-text wrap.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, FrameAnchorType, FrameWrap } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      frame: {
        type: "absolute",
        position: { x: 1440, y: 1440 },
        width: 2880,
        height: 1440,
        anchor: { horizontal: FrameAnchorType.PAGE, vertical: FrameAnchorType.PAGE },
        wrap: FrameWrap.AROUND,
      },
      children: [new TextRun("Framed paragraph")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

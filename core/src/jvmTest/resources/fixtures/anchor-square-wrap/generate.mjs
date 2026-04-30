// Phase 22 first fixture. Floating image with square wrap,
// column-relative offsets at 0/0, no behind-doc.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  Document, Packer, Paragraph, ImageRun,
  HorizontalPositionRelativeFrom, VerticalPositionRelativeFrom,
  TextWrappingType, TextWrappingSide,
} = require("/opt/docx-ref");

// 2x2 PNG bytes (valid header, IDAT may have wrong CRC — XMLUnit
// doesn't care).
const png = Buffer.from(
  "89504e470d0a1a0a0000000d49484452000000020000000208060000007268aacf0000001349444154789c63646060000000000005000150a07ca10000000049454e44ae426082",
  "hex"
);

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new ImageRun({
        type: "png",
        data: png,
        transformation: { width: 200, height: 100 },
        floating: {
          horizontalPosition: { relative: HorizontalPositionRelativeFrom.COLUMN, offset: 0 },
          verticalPosition:   { relative: VerticalPositionRelativeFrom.PARAGRAPH, offset: 0 },
          wrap: { type: TextWrappingType.SQUARE, side: TextWrappingSide.BOTH_SIDES },
          behindDocument: false,
        },
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

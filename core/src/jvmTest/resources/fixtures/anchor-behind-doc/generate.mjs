// Phase 22 fifth fixture. wrapNone + behindDoc=true. Image
// sits behind body text — watermark-style use.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  Document, Packer, Paragraph, ImageRun,
  HorizontalPositionRelativeFrom, VerticalPositionRelativeFrom,
  HorizontalPositionAlign, VerticalPositionAlign,
  TextWrappingType,
} = require("/opt/docx-ref");

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
          horizontalPosition: {
            relative: HorizontalPositionRelativeFrom.PAGE,
            align: HorizontalPositionAlign.CENTER,
          },
          verticalPosition: {
            relative: VerticalPositionRelativeFrom.PAGE,
            align: VerticalPositionAlign.CENTER,
          },
          wrap: { type: TextWrappingType.NONE },
          behindDocument: true,
        },
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

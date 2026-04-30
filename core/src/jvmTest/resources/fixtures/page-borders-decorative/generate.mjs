// Phase 28: page-borders-decorative
// Section with all four sides single 24-eighth-points red,
// display=allPages, offsetFrom=page, zOrder=front.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const {
  Document, Packer, Paragraph, TextRun,
  BorderStyle, PageBorderDisplay, PageBorderOffsetFrom, PageBorderZOrder,
} = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      page: {
        borders: {
          pageBorders: {
            display: PageBorderDisplay.ALL_PAGES,
            offsetFrom: PageBorderOffsetFrom.PAGE,
            zOrder: PageBorderZOrder.FRONT,
          },
          pageBorderTop:    { style: BorderStyle.SINGLE, size: 24, color: "C00000", space: 24 },
          pageBorderLeft:   { style: BorderStyle.SINGLE, size: 24, color: "C00000", space: 24 },
          pageBorderBottom: { style: BorderStyle.SINGLE, size: 24, color: "C00000", space: 24 },
          pageBorderRight:  { style: BorderStyle.SINGLE, size: 24, color: "C00000", space: 24 },
        },
      },
    },
    children: [new Paragraph({ children: [new TextRun("page borders")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

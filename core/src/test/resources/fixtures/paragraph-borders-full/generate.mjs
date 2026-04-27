// All five sides of <w:pBdr>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, BorderStyle } = require("/opt/docx-ref");

const side = (color) => ({ style: BorderStyle.SINGLE, size: 6, color });

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      border: {
        top: side("FF0000"),
        bottom: side("00FF00"),
        left: side("0000FF"),
        right: side("FFFF00"),
        between: { style: BorderStyle.SINGLE, size: 4, color: "auto" },
      },
      children: [new TextRun("bordered")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

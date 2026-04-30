// <w:shd> at paragraph level.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, ShadingType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      shading: { type: ShadingType.CLEAR, color: "auto", fill: "EEEEEE" },
      children: [new TextRun("shaded")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

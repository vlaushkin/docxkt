// Run-level border + shading.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, BorderStyle, ShadingType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun({
      text: "bordered-shaded",
      border: { style: BorderStyle.SINGLE, size: 4, color: "FF0000" },
      shading: { type: ShadingType.CLEAR, color: "auto", fill: "EEEEEE" },
    })] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// bidi + contextualSpacing + outlineLvl=2 + suppressLineNumbers.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      bidirectional: true,
      contextualSpacing: true,
      outlineLevel: 2,
      suppressLineNumbers: true,
      children: [new TextRun("combo")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Single paragraph with keepNext=true — OnOff inside <w:pPr>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        new Paragraph({
          keepNext: true,
          children: [new TextRun("StaysWithNext")],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

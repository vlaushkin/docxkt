// Two runs in one paragraph:
//   run 1: bold=true, italics=true — exercises the attribute-free OnOff form.
//   run 2: bold=false            — exercises the w:val="false" OnOff form.
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
          children: [
            new TextRun({ text: "BoldItalic", bold: true, italics: true }),
            new TextRun({ text: "NotBold", bold: false }),
          ],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

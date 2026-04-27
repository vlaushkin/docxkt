// Three runs:
//   1. strike + smallCaps + superScript — multiple OnOffs + vertAlign
//   2. plain run with no formatting — confirms no empty <w:rPr> leaks
//   3. allCaps = false — OnOff false form on the allCaps (w:caps) element
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
            new TextRun({
              text: "Strikethrough",
              strike: true,
              smallCaps: true,
              superScript: true,
            }),
            new TextRun({ text: " plain " }),
            new TextRun({ text: "NoCaps", allCaps: false }),
          ],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

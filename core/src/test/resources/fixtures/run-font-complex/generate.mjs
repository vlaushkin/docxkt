// Per-script font attributes — each of ascii, cs, eastAsia, hAnsi set
// independently.
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
              text: "PerScript",
              font: {
                ascii: "Calibri",
                hAnsi: "Cambria",
                cs: "Arial",
                eastAsia: "MS Mincho",
              },
            }),
          ],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

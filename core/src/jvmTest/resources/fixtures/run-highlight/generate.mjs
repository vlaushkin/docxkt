// Highlight from the closed HighlightColor enum.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, HighlightColor } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        new Paragraph({
          children: [
            new TextRun({ text: "Highlighted", highlight: HighlightColor.YELLOW }),
          ],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

// Paragraph with indent: left, right, firstLine.
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
          indent: { left: 720, right: 360, firstLine: 240 },
          children: [new TextRun("Indented")],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

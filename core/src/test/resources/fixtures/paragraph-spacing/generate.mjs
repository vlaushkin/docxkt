// Spacing: before/after/line with lineRule=auto.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, LineRuleType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        new Paragraph({
          spacing: { before: 120, after: 240, line: 360, lineRule: LineRuleType.AUTO },
          children: [new TextRun("Spaced")],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

// Two paragraphs:
//   1. alignment + indent + spacing + keepNext — canonical child order.
//   2. plain paragraph — verifies empty <w:pPr/> suppression.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, AlignmentType, LineRuleType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {},
      children: [
        new Paragraph({
          alignment: AlignmentType.JUSTIFIED,
          indent: { left: 720, firstLine: 360 },
          spacing: { before: 120, after: 120, line: 240, lineRule: LineRuleType.AUTO },
          keepNext: true,
          children: [new TextRun("Combo")],
        }),
        new Paragraph({
          children: [new TextRun("Plain")],
        }),
      ],
    },
  ],
});

Packer.toBuffer(doc).then((buf) => fs.writeFileSync(process.argv[2], buf));

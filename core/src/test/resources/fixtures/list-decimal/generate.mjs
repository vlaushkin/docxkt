// Decimal list with three levels; three paragraphs at levels 0/1/2.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, AlignmentType, LevelFormat } = require("/opt/docx-ref");

const doc = new Document({
  numbering: {
    config: [{
      reference: "my-list",
      levels: [
        { level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
        { level: 1, format: LevelFormat.DECIMAL, text: "%2.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 1440, hanging: 360 } } } },
        { level: 2, format: LevelFormat.DECIMAL, text: "%3.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 2160, hanging: 360 } } } },
      ],
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ numbering: { reference: "my-list", level: 0 }, children: [new TextRun("alpha")] }),
    new Paragraph({ numbering: { reference: "my-list", level: 1 }, children: [new TextRun("beta")] }),
    new Paragraph({ numbering: { reference: "my-list", level: 2 }, children: [new TextRun("gamma")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

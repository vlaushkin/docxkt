// Lower-roman list, single level — exercises a second LevelFormat value.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, AlignmentType, LevelFormat } = require("/opt/docx-ref");

const doc = new Document({
  numbering: {
    config: [{
      reference: "roman",
      levels: [
        { level: 0, format: LevelFormat.LOWER_ROMAN, text: "%1.", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
      ],
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ numbering: { reference: "roman", level: 0 }, children: [new TextRun("first")] }),
    new Paragraph({ numbering: { reference: "roman", level: 0 }, children: [new TextRun("second")] }),
    new Paragraph({ numbering: { reference: "roman", level: 0 }, children: [new TextRun("third")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

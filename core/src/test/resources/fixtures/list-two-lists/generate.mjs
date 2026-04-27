// Two distinct list templates coexisting in one document —
// exercises the allocator's ability to assign sequential
// abstractNumIds and numIds across multiple registrations.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, AlignmentType, LevelFormat } = require("/opt/docx-ref");

const doc = new Document({
  numbering: {
    config: [
      {
        reference: "decimal-list",
        levels: [
          { level: 0, format: LevelFormat.DECIMAL, text: "%1.", alignment: AlignmentType.LEFT,
            style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
        ],
      },
      {
        reference: "bullet-list",
        levels: [
          { level: 0, format: LevelFormat.BULLET, text: "●", alignment: AlignmentType.LEFT,
            style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
        ],
      },
    ],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ numbering: { reference: "decimal-list", level: 0 }, children: [new TextRun("num-one")] }),
    new Paragraph({ numbering: { reference: "decimal-list", level: 0 }, children: [new TextRun("num-two")] }),
    new Paragraph({ numbering: { reference: "bullet-list", level: 0 }, children: [new TextRun("bullet-one")] }),
    new Paragraph({ numbering: { reference: "bullet-list", level: 0 }, children: [new TextRun("bullet-two")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

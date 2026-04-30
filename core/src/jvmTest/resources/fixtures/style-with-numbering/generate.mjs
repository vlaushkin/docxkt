// Paragraph style (BulletItem) that sets numbering in its
// paragraph-level properties, combined with a numbering template.
// Exercises Phase 10 (numbering) × Phase 11 (styles) interaction
// and locks the auto-ListParagraph promotion behaviour when an
// explicit user style is set.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, AlignmentType, LevelFormat } = require("/opt/docx-ref");

const doc = new Document({
  numbering: {
    config: [{
      reference: "my-bullets",
      levels: [
        { level: 0, format: LevelFormat.BULLET, text: "●", alignment: AlignmentType.LEFT,
          style: { paragraph: { indent: { left: 720, hanging: 360 } } } },
      ],
    }],
  },
  styles: {
    paragraphStyles: [{
      id: "BulletItem",
      name: "Bullet Item",
      basedOn: "Normal",
      run: { italics: true },
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ style: "BulletItem", numbering: { reference: "my-bullets", level: 0 },
                    children: [new TextRun("first")] }),
    new Paragraph({ style: "BulletItem", numbering: { reference: "my-bullets", level: 0 },
                    children: [new TextRun("second")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

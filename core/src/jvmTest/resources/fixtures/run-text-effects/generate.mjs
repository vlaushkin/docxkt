// emboss + imprint=false + vanish + textEffect + emphasisMark.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, TextEffect, EmphasisMarkType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun({
      text: "effects",
      emboss: true,
      imprint: false,
      vanish: true,
      effect: TextEffect.SHIMMER,
      emphasisMark: { type: EmphasisMarkType.DOT },
    })] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

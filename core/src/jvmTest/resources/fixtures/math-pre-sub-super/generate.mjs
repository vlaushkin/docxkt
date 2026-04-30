// Phase 27: ⁱTⱼ — pre-sub-super script (tensor notation).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Math, MathRun, MathPreSubSuperScript } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Math({ children: [
        new MathPreSubSuperScript({
          children: [new MathRun("T")],
          subScript: [new MathRun("i")],
          superScript: [new MathRun("j")],
        }),
      ] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

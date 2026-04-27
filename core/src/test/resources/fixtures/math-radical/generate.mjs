// Phase 26 first fixture. Square root + cube root in two
// paragraphs.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Math, MathRun, MathRadical } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Math({ children: [
        new MathRadical({ children: [new MathRun("x + 1")] }),
      ] }),
    ] }),
    new Paragraph({ children: [
      new Math({ children: [
        new MathRadical({ children: [new MathRun("y")], degree: [new MathRun("3")] }),
      ] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

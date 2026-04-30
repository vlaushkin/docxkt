// Phase 27: sin(x)
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Math, MathRun, MathFunction } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Math({ children: [
        new MathFunction({
          name: [new MathRun("sin")],
          children: [new MathRun("x")],
        }),
      ] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

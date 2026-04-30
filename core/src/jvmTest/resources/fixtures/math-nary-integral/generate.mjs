// Phase 27: integral from 0 to 1 of f(x) dx.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, Math, MathRun, MathIntegral } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Math({ children: [
        new MathIntegral({
          children: [new MathRun("f(x) dx")],
          subScript: [new MathRun("0")],
          superScript: [new MathRun("1")],
        }),
      ] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

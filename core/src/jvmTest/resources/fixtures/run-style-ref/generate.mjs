// rStyle + noProof=true + snapToGrid=false + rightToLeft=true.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun({
      text: "styled",
      style: "Heading1",
      noProof: true,
      snapToGrid: false,
      rightToLeft: true,
    })] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

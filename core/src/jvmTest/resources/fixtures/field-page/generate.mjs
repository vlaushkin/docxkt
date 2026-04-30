// Phase 15 second fixture. Complex-form PAGE field inside a
// paragraph that also has surrounding runs.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, PageNumber } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Page "),
      new TextRun({ children: [PageNumber.CURRENT] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

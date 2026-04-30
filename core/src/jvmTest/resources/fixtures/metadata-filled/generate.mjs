// Phase 14 second fixture. Fully populated properties block —
// every Dublin-Core element emitted.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  title: "My Document",
  subject: "A sample",
  creator: "Vasily",
  keywords: "docxkt, sample",
  description: "A fully populated core-properties fixture",
  lastModifiedBy: "Vasily",
  revision: 3,
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun("metadata filled")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

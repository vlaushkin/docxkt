// Phase 20 first fixture. One <w:ins> wrapping a text run.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, InsertedTextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Kept. "),
      new InsertedTextRun({ id: 1, author: "A", date: "2026-04-24T00:00:00Z", text: "added" }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

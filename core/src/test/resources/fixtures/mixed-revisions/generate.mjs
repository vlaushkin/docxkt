// Phase 20 stretch fixture. Mixed ins + del in one paragraph.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, InsertedTextRun, DeletedTextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Before "),
      new InsertedTextRun({ id: 1, author: "A", date: "2026-04-24T00:00:00Z", text: "plus" }),
      new TextRun(" "),
      new DeletedTextRun({ id: 2, author: "A", date: "2026-04-24T00:00:00Z", text: "minus" }),
      new TextRun(" after"),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

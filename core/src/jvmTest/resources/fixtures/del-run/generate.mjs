// Phase 20 second fixture. One <w:del> wrapping a deleted
// text run — inner <w:t> becomes <w:delText>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, DeletedTextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Kept. "),
      new DeletedTextRun({ id: 1, author: "A", date: "2026-04-24T00:00:00Z", text: "removed" }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

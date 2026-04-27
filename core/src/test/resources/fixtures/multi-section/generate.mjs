// Phase 28: multi-section — two sections, each with its own pgSz.
// The first section ends at paragraph 1 (portrait); the second
// section spans paragraph 2 (landscape).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, PageOrientation } = require("/opt/docx-ref");

const doc = new Document({
  sections: [
    {
      properties: {
        page: { size: { orientation: PageOrientation.PORTRAIT } },
      },
      children: [new Paragraph({ children: [new TextRun("Section 1 portrait")] })],
    },
    {
      properties: {
        page: { size: { orientation: PageOrientation.LANDSCAPE } },
      },
      children: [new Paragraph({ children: [new TextRun("Section 2 landscape")] })],
    },
  ],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

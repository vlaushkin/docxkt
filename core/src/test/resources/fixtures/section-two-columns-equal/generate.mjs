// Phase 35a: section-two-columns-equal — 2 equal-width columns,
// 720-twip gap, no separator.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      column: { count: 2, space: 720, equalWidth: true },
    },
    children: [new Paragraph({ children: [new TextRun("two columns")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

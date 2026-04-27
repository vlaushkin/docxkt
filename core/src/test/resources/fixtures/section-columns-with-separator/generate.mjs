// Phase 35a: section-columns-with-separator — 2 equal cols + sep flag.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      column: { count: 2, space: 720, equalWidth: true, separate: true },
    },
    children: [new Paragraph({ children: [new TextRun("with separator")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

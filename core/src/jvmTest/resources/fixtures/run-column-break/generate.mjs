// Phase 29: run-column-break — <w:br w:type="column"/> via the
// upstream-public ColumnBreak helper (which renders as a bare run
// containing just the typed <w:br/>).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, ColumnBreak } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [new Paragraph({ children: [
      new TextRun("col1"),
      new ColumnBreak(),
      new TextRun("col2"),
    ]})],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

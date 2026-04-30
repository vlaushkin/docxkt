// Phase 35a: section-three-columns-custom-widths — three columns
// with explicit widths via Column instances (not plain objects, which
// trip an upstream serialization bug).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Column } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      column: {
        count: 3,
        equalWidth: false,
        children: [
          new Column({ width: 3000, space: 360 }),
          new Column({ width: 4000, space: 360 }),
          new Column({ width: 2500 }),
        ],
      },
    },
    children: [new Paragraph({ children: [new TextRun("custom widths")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

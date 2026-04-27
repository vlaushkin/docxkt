// Phase 35c: section-line-numbering — <w:lnNumType> with
// countBy=1, start=1, distance=720, restart=continuous.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, LineNumberRestartFormat } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      lineNumbers: {
        countBy: 1,
        start: 1,
        distance: 720,
        restart: LineNumberRestartFormat.CONTINUOUS,
      },
    },
    children: [new Paragraph({ children: [new TextRun("with line numbers")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

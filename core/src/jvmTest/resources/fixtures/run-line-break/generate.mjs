// Soft line break inside a run — <w:br/> with no w:type. Using the
// children-array form to place the break after the text (matches
// what our DSL's text("...") { lineBreak() } produces).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, createBreak } = require("/opt/docx-ref");
// The named export createBreak isn't re-exported; but the upstream
// `TextRun.children` array admits a mix. Use the `break` count after
// the text instead — TextRun emits breaks first (see run.ts). To
// match our DSL's "text then break" order, split into two runs: a
// plain text run and a break-only run.

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Paragraph({
        children: [
          new TextRun("line1"),
          new TextRun({ break: 1 }),
          new TextRun("line2"),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

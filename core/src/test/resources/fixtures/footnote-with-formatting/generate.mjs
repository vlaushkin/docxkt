// Phase 17 fourth fixture. Footnote content includes bold rPr
// — asserts run-level formatting round-trips inside the
// footnote body.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, FootnoteReferenceRun } = require("/opt/docx-ref");

const doc = new Document({
  footnotes: {
    1: { children: [new Paragraph({ children: [
      new TextRun({ text: "Bold fact", bold: true }),
      new TextRun(" — source: x."),
    ] }) ] },
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Claim"),
      new FootnoteReferenceRun(1),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

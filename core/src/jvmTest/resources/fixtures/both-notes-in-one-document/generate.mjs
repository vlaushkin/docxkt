// Phase 17 stretch fixture. Footnote + endnote in one doc.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, FootnoteReferenceRun, EndnoteReferenceRun } = require("/opt/docx-ref");

const doc = new Document({
  footnotes: {
    1: { children: [new Paragraph({ children: [new TextRun("Footnote.")] }) ] },
  },
  endnotes: {
    1: { children: [new Paragraph({ children: [new TextRun("Endnote.")] }) ] },
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Claim"),
      new FootnoteReferenceRun(1),
    ] }),
    new Paragraph({ children: [
      new TextRun("Aside"),
      new EndnoteReferenceRun(1),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

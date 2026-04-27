// Phase 17 first fixture. One user footnote referenced from
// one body paragraph.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, FootnoteReferenceRun } = require("/opt/docx-ref");

const doc = new Document({
  footnotes: {
    1: { children: [new Paragraph({ children: [new TextRun("First note.")] }) ] },
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Claim"),
      new FootnoteReferenceRun(1),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

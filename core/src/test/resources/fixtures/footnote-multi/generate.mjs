// Phase 17 third fixture. Two user footnotes from two
// paragraphs.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, FootnoteReferenceRun } = require("/opt/docx-ref");

const doc = new Document({
  footnotes: {
    1: { children: [new Paragraph({ children: [new TextRun("Note one.")] }) ] },
    2: { children: [new Paragraph({ children: [new TextRun("Note two.")] }) ] },
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("First"),
      new FootnoteReferenceRun(1),
    ] }),
    new Paragraph({ children: [
      new TextRun("Second"),
      new FootnoteReferenceRun(2),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

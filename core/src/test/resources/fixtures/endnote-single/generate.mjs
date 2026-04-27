// Phase 17 second fixture. One user endnote referenced from
// one body paragraph.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, EndnoteReferenceRun } = require("/opt/docx-ref");

const doc = new Document({
  endnotes: {
    1: { children: [new Paragraph({ children: [new TextRun("End note.")] }) ] },
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("See"),
      new EndnoteReferenceRun(1),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

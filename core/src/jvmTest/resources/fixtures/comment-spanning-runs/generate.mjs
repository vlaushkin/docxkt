// Phase 19 second fixture. Comment spanning two runs (with
// distinct rPr). Asserts commentRangeStart/End brackets work
// across mid-run boundaries.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, CommentRangeStart, CommentRangeEnd, CommentReference } = require("/opt/docx-ref");

const doc = new Document({
  comments: {
    children: [{
      id: 0,
      author: "Bob",
      date: new Date("2026-04-24T00:00:00.000Z"),
      children: [new Paragraph({ children: [new TextRun("Applies to both bold and plain.")] })],
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new CommentRangeStart(0),
      new TextRun({ text: "bold", bold: true }),
      new TextRun(" and plain"),
      new CommentRangeEnd(0),
      new TextRun({ children: [new CommentReference(0)] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

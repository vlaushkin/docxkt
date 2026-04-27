// Phase 19 first fixture. One comment from one author
// spanning a single text run.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, CommentRangeStart, CommentRangeEnd, CommentReference } = require("/opt/docx-ref");

const doc = new Document({
  comments: {
    children: [{
      id: 0,
      author: "Alice",
      initials: "AB",
      date: new Date("2026-04-24T00:00:00.000Z"),
      children: [new Paragraph({ children: [new TextRun("A remark.")] })],
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new CommentRangeStart(0),
      new TextRun("spanned text"),
      new CommentRangeEnd(0),
      new TextRun({ children: [new CommentReference(0)] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

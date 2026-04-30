// Phase 19 stretch fixture. Two comments from different
// authors referenced from two body paragraphs.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, CommentRangeStart, CommentRangeEnd, CommentReference } = require("/opt/docx-ref");

const doc = new Document({
  comments: {
    children: [
      {
        id: 0,
        author: "Alice",
        initials: "AB",
        date: new Date("2026-04-24T00:00:00.000Z"),
        children: [new Paragraph({ children: [new TextRun("Alice's note.")] })],
      },
      {
        id: 1,
        author: "Bob",
        initials: "BM",
        date: new Date("2026-04-24T00:00:00.000Z"),
        children: [new Paragraph({ children: [new TextRun("Bob's note.")] })],
      },
    ],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new CommentRangeStart(0),
      new TextRun("first span"),
      new CommentRangeEnd(0),
      new TextRun({ children: [new CommentReference(0)] }),
    ] }),
    new Paragraph({ children: [
      new CommentRangeStart(1),
      new TextRun("second span"),
      new CommentRangeEnd(1),
      new TextRun({ children: [new CommentReference(1)] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Bookmark spanning two paragraphs — bookmarkStart in the first
// paragraph, bookmarkEnd in the second. Upstream represents this
// by splitting a Bookmark's start/children/end manually.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, BookmarkStart, BookmarkEnd } = require("/opt/docx-ref");

// Upstream's BookmarkStart/BookmarkEnd take a numeric linkId that
// must match across start and end. Using 1 for both sides to match
// our per-document counter.
const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new BookmarkStart("section", 1),
      new TextRun("Start of section"),
    ] }),
    new Paragraph({ children: [
      new TextRun("End of section"),
      new BookmarkEnd(1),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

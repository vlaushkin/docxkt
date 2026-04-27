// Phase 18 first fixture. Minimal TOC with hyperlink +
// headingStyleRange flags.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, TableOfContents } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new TableOfContents("Contents", { hyperlink: true, headingStyleRange: "1-3" }),
    new Paragraph({ children: [new TextRun("body")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

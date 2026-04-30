// Phase 16 second fixture. Footer "Page N of M" with PAGE
// and NUMPAGES complex fields — exercises both wrappers.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Footer, AlignmentType, PageNumber } = require("/opt/docx-ref");

const footer = new Footer({
  children: [
    new Paragraph({ alignment: AlignmentType.CENTER, children: [
      new TextRun("Page "),
      new TextRun({ children: [PageNumber.CURRENT] }),
      new TextRun(" of "),
      new TextRun({ children: [PageNumber.TOTAL_PAGES] }),
    ] }),
  ],
});

const doc = new Document({
  sections: [{
    properties: {},
    footers: { default: footer },
    children: [ new Paragraph({ children: [new TextRun("body")] }) ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

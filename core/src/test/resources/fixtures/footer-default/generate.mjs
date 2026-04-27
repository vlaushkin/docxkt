// Footer with one paragraph; no header.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Footer } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    footers: { default: new Footer({ children: [new Paragraph({ children: [new TextRun("footer text")] })] }) },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

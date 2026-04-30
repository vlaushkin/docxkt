// Header with one paragraph; no footer.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    headers: { default: new Header({ children: [new Paragraph({ children: [new TextRun("header text")] })] }) },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Both header and footer present.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header, Footer } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    headers: { default: new Header({ children: [new Paragraph({ children: [new TextRun("hdr")] })] }) },
    footers: { default: new Footer({ children: [new Paragraph({ children: [new TextRun("ftr")] })] }) },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

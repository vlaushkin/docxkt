// Header + footer + body image — forces the rId allocator to
// allocate three kinds of relationships in one document.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header, Footer, ImageRun } = require("/opt/docx-ref");

const imgBytes = fs.readFileSync("/opt/fixtures/tiny.png");

const doc = new Document({
  sections: [{
    properties: {},
    headers: { default: new Header({ children: [new Paragraph({ children: [new TextRun("hdr")] })] }) },
    footers: { default: new Footer({ children: [new Paragraph({ children: [new TextRun("ftr")] })] }) },
    children: [
      new Paragraph({
        children: [
          new ImageRun({
            data: imgBytes,
            type: "png",
            transformation: { width: 100, height: 100 },
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

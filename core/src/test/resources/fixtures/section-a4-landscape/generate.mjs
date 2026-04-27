// A4 landscape: dimensions swap + orient=landscape.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, PageOrientation } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: { page: { size: { orientation: PageOrientation.LANDSCAPE } } },
    children: [new Paragraph({ children: [new TextRun("landscape")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

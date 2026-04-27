// UTF-8 supplementary-plane smoke: emoji + non-BMP CJK.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Paragraph({
        children: [
          new TextRun("Hello \u{1F600}"),  // U+1F600 😀
          new TextRun("\u{20BB7}"),          // U+20BB7 𠮷 (non-BMP CJK)
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

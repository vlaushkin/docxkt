// Insurance: emoji + non-BMP CJK inside a header. Mirrors Phase 5's
// utf8-supplementary but exercises the header code path.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    headers: {
      default: new Header({
        children: [
          new Paragraph({
            children: [
              new TextRun("Caption \u{1F600}"),  // U+1F600 😀
              new TextRun("\u{20BB7}"),           // U+20BB7 𠮷 (non-BMP CJK)
            ],
          }),
        ],
      }),
    },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

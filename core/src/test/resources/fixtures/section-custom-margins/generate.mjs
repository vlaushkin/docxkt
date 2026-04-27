// Non-default pgMar values.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {
      page: {
        margin: {
          top: 720, right: 1000, bottom: 720, left: 1000,
          header: 360, footer: 360, gutter: 0,
        },
      },
    },
    children: [new Paragraph({ children: [new TextRun("margins")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

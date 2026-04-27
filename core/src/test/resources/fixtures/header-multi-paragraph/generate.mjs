// Header with two paragraphs.
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
          new Paragraph({ children: [new TextRun("Title")] }),
          new Paragraph({ children: [new TextRun("Subtitle")] }),
        ],
      }),
    },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

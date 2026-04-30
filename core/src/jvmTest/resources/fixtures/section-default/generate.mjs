// Trivial document with no section configuration — exercises the
// default <w:sectPr> block upstream always emits.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [new Paragraph({ children: [new TextRun("default")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

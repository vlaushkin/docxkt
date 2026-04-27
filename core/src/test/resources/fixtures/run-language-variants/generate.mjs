// Two runs: full language triple, then just w:val.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun({
        text: "full",
        language: { value: "en-US", eastAsia: "ja-JP", bidirectional: "ar-SA" },
      }),
      new TextRun({
        text: "partial",
        language: { value: "en-US" },
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

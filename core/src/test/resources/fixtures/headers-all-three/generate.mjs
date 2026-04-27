// Phase 28: headers-all-three — default + first + even.
// Both <w:titlePg/> AND <w:evenAndOddHeaders/> active.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header } = require("/opt/docx-ref");

const doc = new Document({
  evenAndOddHeaderAndFooters: true,
  sections: [{
    properties: { titlePage: true },
    headers: {
      default: new Header({ children: [new Paragraph({ children: [new TextRun("Default")] })] }),
      first:   new Header({ children: [new Paragraph({ children: [new TextRun("First")] })] }),
      even:    new Header({ children: [new Paragraph({ children: [new TextRun("Even")] })] }),
    },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

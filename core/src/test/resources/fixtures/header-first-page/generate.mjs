// Phase 28: header-first-page — section with default + first headers,
// titlePg flag.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header, Footer } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: { titlePage: true },
    headers: {
      default: new Header({ children: [new Paragraph({ children: [new TextRun("Default header")] })] }),
      first:   new Header({ children: [new Paragraph({ children: [new TextRun("First-page header")] })] }),
    },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Phase 28: header-even-pages — section with default + even headers,
// settings.xml carries <w:evenAndOddHeaders/>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Header } = require("/opt/docx-ref");

const doc = new Document({
  evenAndOddHeaderAndFooters: true,
  sections: [{
    properties: {},
    headers: {
      default: new Header({ children: [new Paragraph({ children: [new TextRun("Default header")] })] }),
      even:    new Header({ children: [new Paragraph({ children: [new TextRun("Even-page header")] })] }),
    },
    children: [new Paragraph({ children: [new TextRun("body")] })],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

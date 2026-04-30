// Single external hyperlink wrapping one plain-text run inside a
// paragraph. Minimal Phase 12 wire: <w:hyperlink r:id w:history="1">
// around one <w:r>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, ExternalHyperlink } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Visit "),
      new ExternalHyperlink({
        link: "https://example.com",
        children: [new TextRun("example.com")],
      }),
      new TextRun(" for more."),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

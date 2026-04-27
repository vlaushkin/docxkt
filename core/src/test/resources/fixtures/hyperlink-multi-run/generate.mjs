// One hyperlink wrapping two runs with distinct rPr. Asserts
// the <w:hyperlink> container doesn't merge or coalesce its
// children — each <w:r> keeps its own <w:rPr>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, ExternalHyperlink } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new ExternalHyperlink({
        link: "https://example.com",
        children: [
          new TextRun({ text: "bold ", bold: true }),
          new TextRun({ text: "plain" }),
        ],
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

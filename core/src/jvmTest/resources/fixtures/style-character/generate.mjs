// One user character style (Emphasis, italics rPr);
// one paragraph mixing a plain run and an emphasis-styled run.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  styles: {
    characterStyles: [{
      id: "Emphasis",
      name: "Emphasis",
      run: { italics: true },
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("See "),
      new TextRun({ text: "this", style: "Emphasis" }),
      new TextRun(" for details."),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

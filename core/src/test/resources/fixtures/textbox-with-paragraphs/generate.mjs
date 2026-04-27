// Phase 23 second fixture. Multiple paragraphs in one textbox,
// one with bold rPr.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, WpsShapeRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new WpsShapeRun({
        type: "wps",
        transformation: { width: 250, height: 150 },
        children: [
          new Paragraph({ children: [new TextRun({ text: "Title", bold: true })] }),
          new Paragraph({ children: [new TextRun("First body line.")] }),
          new Paragraph({ children: [new TextRun("Second body line.")] }),
        ],
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

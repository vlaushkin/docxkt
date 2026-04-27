// Phase 23 first fixture. Minimal textbox with one paragraph.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, WpsShapeRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new WpsShapeRun({
        type: "wps",
        transformation: { width: 200, height: 100 },
        children: [
          new Paragraph({ children: [new TextRun("Inside textbox")] }),
        ],
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Phase 23 third fixture. Textbox with verticalAnchor=CENTER
// and non-zero body margins. Stress-tests <wps:bodyPr>
// attribute emission.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, WpsShapeRun, VerticalAnchor } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new WpsShapeRun({
        type: "wps",
        transformation: { width: 200, height: 100 },
        children: [
          new Paragraph({ children: [new TextRun("Centered")] }),
        ],
        bodyProperties: {
          verticalAnchor: VerticalAnchor.CENTER,
          margins: { top: 91440, bottom: 91440, left: 91440, right: 91440 },
        },
      }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

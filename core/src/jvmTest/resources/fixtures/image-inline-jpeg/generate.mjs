// Inline JPEG image inside a run.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, ImageRun } = require("/opt/docx-ref");

const imgBytes = fs.readFileSync("/opt/fixtures/tiny.jpg");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Paragraph({
        children: [
          new ImageRun({
            data: imgBytes,
            type: "jpg",
            transformation: { width: 100, height: 100 },
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Three tab stops: left, center, right+dot-leader.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, TabStopType, LeaderType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({
      tabStops: [
        { type: TabStopType.LEFT, position: 2000 },
        { type: TabStopType.CENTER, position: 4000 },
        { type: TabStopType.RIGHT, position: 9000, leader: LeaderType.DOT },
      ],
      children: [new TextRun("a\tb\tc")],
    }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

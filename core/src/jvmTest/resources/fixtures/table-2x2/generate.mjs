// 2 rows x 2 cols with no explicit properties.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell } = require("/opt/docx-ref");

function mkCell(t) {
  return new TableCell({ children: [new Paragraph({ children: [new TextRun(t)] })] });
}

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        rows: [
          new TableRow({ children: [mkCell("A1"), mkCell("B1")] }),
          new TableRow({ children: [mkCell("A2"), mkCell("B2")] }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

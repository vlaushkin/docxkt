// tblW using PCT — 5000 means 100%.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, WidthType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        width: { size: 5000, type: WidthType.PERCENTAGE },
        rows: [
          new TableRow({
            children: [
              new TableCell({ children: [new Paragraph({ children: [new TextRun("P")] })] }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

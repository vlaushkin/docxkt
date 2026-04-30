// vMerge restart + continue across two rows.
// Row 1: [restart | data]
// Row 2: [continue | data]
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, VerticalMergeType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        columnWidths: [1500, 1500],
        rows: [
          new TableRow({
            children: [
              new TableCell({
                verticalMerge: VerticalMergeType.RESTART,
                children: [new Paragraph({ children: [new TextRun("Tall")] })],
              }),
              new TableCell({ children: [new Paragraph({ children: [new TextRun("R1")] })] }),
            ],
          }),
          new TableRow({
            children: [
              new TableCell({
                verticalMerge: VerticalMergeType.CONTINUE,
                children: [],
              }),
              new TableCell({ children: [new Paragraph({ children: [new TextRun("R2")] })] }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

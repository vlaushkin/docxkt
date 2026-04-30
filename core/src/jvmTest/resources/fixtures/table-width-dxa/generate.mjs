// tblW + tcW with dxa (twips).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, WidthType } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        width: { size: 5000, type: WidthType.DXA },
        columnWidths: [2500, 2500],
        rows: [
          new TableRow({
            children: [
              new TableCell({
                width: { size: 2500, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("L")] })],
              }),
              new TableCell({
                width: { size: 2500, type: WidthType.DXA },
                children: [new Paragraph({ children: [new TextRun("R")] })],
              }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

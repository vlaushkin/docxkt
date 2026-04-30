// Row where one cell spans two grid columns.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell } = require("/opt/docx-ref");

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
                columnSpan: 2,
                children: [new Paragraph({ children: [new TextRun("Spanned")] })],
              }),
            ],
          }),
          new TableRow({
            children: [
              new TableCell({ children: [new Paragraph({ children: [new TextRun("L")] })] }),
              new TableCell({ children: [new Paragraph({ children: [new TextRun("R")] })] }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

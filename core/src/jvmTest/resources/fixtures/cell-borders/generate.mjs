// tcBorders with a mix of sides; confirms only user-set sides emit.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, BorderStyle } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        rows: [
          new TableRow({
            children: [
              new TableCell({
                borders: {
                  top: { style: BorderStyle.THICK, size: 16, color: "00FF00" },
                  bottom: { style: BorderStyle.DASHED, size: 8, color: "0000FF" },
                },
                children: [new Paragraph({ children: [new TextRun("C")] })],
              }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

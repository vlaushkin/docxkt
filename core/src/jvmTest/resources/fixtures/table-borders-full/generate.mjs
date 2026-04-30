// All six sides of <w:tblBorders> explicitly set to a non-default style.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, BorderStyle } = require("/opt/docx-ref");

const side = { style: BorderStyle.DOUBLE, size: 12, color: "3366FF" };
const inside = { style: BorderStyle.DASHED, size: 8, color: "CCCCCC" };

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        borders: {
          top: side, left: side, bottom: side, right: side,
          insideHorizontal: inside, insideVertical: inside,
        },
        rows: [
          new TableRow({ children: [
            new TableCell({ children: [new Paragraph({ children: [new TextRun("A")] })] }),
            new TableCell({ children: [new Paragraph({ children: [new TextRun("B")] })] }),
          ] }),
          new TableRow({ children: [
            new TableCell({ children: [new Paragraph({ children: [new TextRun("C")] })] }),
            new TableCell({ children: [new Paragraph({ children: [new TextRun("D")] })] }),
          ] }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

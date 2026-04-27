// Only top + bottom set; other sides fall through to upstream defaults.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, BorderStyle } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        borders: {
          top: { style: BorderStyle.THICK, size: 16, color: "FF0000" },
          bottom: { style: BorderStyle.THICK, size: 16, color: "FF0000" },
        },
        rows: [
          new TableRow({ children: [
            new TableCell({ children: [new Paragraph({ children: [new TextRun("P")] })] }),
          ] }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

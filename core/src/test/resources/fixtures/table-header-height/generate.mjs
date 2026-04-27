// Row with tblHeader and trHeight+hRule.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Table, TableRow, TableCell, HeightRule } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [
      new Table({
        rows: [
          new TableRow({
            tableHeader: true,
            height: { value: 500, rule: HeightRule.ATLEAST },
            children: [
              new TableCell({ children: [new Paragraph({ children: [new TextRun("Header")] })] }),
            ],
          }),
        ],
      }),
    ],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

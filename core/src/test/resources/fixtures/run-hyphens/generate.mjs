// Phase 29: run-hyphens — soft hyphen + no-break hyphen in one
// paragraph. Each is its own bare run.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, NoBreakHyphen, SoftHyphen } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{
    properties: {},
    children: [new Paragraph({ children: [
      new TextRun("co"),
      new TextRun({ children: [new SoftHyphen()] }),
      new TextRun("operate"),
      new TextRun(" "),
      new TextRun("up"),
      new TextRun({ children: [new NoBreakHyphen()] }),
      new TextRun("to"),
    ]})],
  }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

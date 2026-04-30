// Phase 15 fourth fixture. SimpleField with cached value —
// asserts <w:fldSimple w:instr="AUTHOR"><w:r><w:t>…</w:t></w:r>
// </w:fldSimple> wire.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, SimpleField } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("By "),
      new SimpleField("AUTHOR", "Vasily"),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

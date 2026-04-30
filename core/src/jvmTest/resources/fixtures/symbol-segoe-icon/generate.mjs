// Phase 21 second fixture. Custom font name via the
// symbolfont option (Segoe UI Symbol glyph).
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, SymbolRun } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new SymbolRun({ char: "2713", symbolfont: "Segoe UI Symbol" }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

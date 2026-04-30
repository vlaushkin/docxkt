// Phase 15 third fixture. Complex-form NUMPAGES field — second
// pagination instruction; asserts the same complex-field wire
// with a different instruction string.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, PageNumber } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new TextRun("Total: "),
      new TextRun({ children: [PageNumber.TOTAL_PAGES] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

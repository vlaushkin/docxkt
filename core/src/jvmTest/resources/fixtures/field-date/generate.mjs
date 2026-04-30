// Phase 15 first fixture. Simple <w:fldSimple> with a DATE
// instruction carrying a format switch — exercises quote
// escaping in the w:instr attribute.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, SimpleField } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new SimpleField('DATE \\@ "MMM d, yyyy"'),
      new TextRun(" — today"),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// One user paragraph style (Heading1), one paragraph referencing it.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  styles: {
    paragraphStyles: [{
      id: "Heading1",
      name: "heading 1",
      basedOn: "Normal",
      run: { bold: true, size: 32 },
    }],
  },
  sections: [{ properties: {}, children: [
    new Paragraph({ style: "Heading1", children: [new TextRun("Introduction")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

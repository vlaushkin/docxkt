// Phase 14 third fixture. Two custom properties, no other
// metadata touches. Exercises docProps/custom.xml emission and
// the _rels/.rels rId4 routing.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun } = require("/opt/docx-ref");

const doc = new Document({
  customProperties: [
    { name: "Department", value: "Engineering" },
    { name: "Project", value: "Alpha" },
  ],
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [new TextRun("custom props only")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

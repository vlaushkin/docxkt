// One paragraph, one bookmark wrapping a single run. Minimal
// Phase 13 wire: <w:bookmarkStart w:id="1" w:name="intro"/>
// followed by <w:r>...</w:r> followed by <w:bookmarkEnd w:id="1"/>.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Bookmark } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Bookmark({ id: "intro", children: [new TextRun("Introduction")] }),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

// Phase 18 second fixture. TOC with headingStyleRange only,
// no hyperlink — asserts the `\h` switch is absent from the
// instruction string.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, TableOfContents } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new TableOfContents("Contents", { headingStyleRange: "1-3" }),
    new Paragraph({ children: [new TextRun("body")] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));

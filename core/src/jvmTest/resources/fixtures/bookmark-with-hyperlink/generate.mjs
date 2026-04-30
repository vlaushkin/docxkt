// A bookmark defined in one paragraph + an internal hyperlink in
// a later paragraph pointing at it. Phase 12 × Phase 13
// interaction via <w:hyperlink w:anchor="…">.
import fs from "node:fs";
import { createRequire } from "node:module";
const require = createRequire(import.meta.url);
const { Document, Packer, Paragraph, TextRun, Bookmark, InternalHyperlink } = require("/opt/docx-ref");

const doc = new Document({
  sections: [{ properties: {}, children: [
    new Paragraph({ children: [
      new Bookmark({ id: "target", children: [new TextRun("Target heading")] }),
    ] }),
    new Paragraph({ children: [
      new TextRun("See "),
      new InternalHyperlink({
        anchor: "target",
        children: [new TextRun("the target")],
      }),
      new TextRun(" above."),
    ] }),
  ] }],
});
Packer.toBuffer(doc).then(buf => fs.writeFileSync(process.argv[2], buf));
